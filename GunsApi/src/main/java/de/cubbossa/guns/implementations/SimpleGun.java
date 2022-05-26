package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.*;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.context.*;
import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

@Getter
@Setter
public class SimpleGun implements Gun {

	private final NamespacedKey key;
	private ItemStack itemStack = new ItemStack(Material.STONE_HOE);
	private String name = "Just a Gun";
	private List<String> lore = new ArrayList<>();
	private Predicate<Player> usePredicate = p -> true;

	private List<Attachment> attachments = new ArrayList<>();
	private List<Ammunition> ammunition = new ArrayList<>();
	private Supplier<EffectPlayer> muzzleFlashFactory = () -> new SoundPlayer().withSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF);
	private Supplier<EffectPlayer> rechargeEffectFactory = EffectPlayer::new;
	private Supplier<EffectPlayer> noAmmunitionEffectFactory = EffectPlayer::new;
	private Map<GunAction<?>, ContextConsumer<?>> actionMap = new HashMap<>();

	public SimpleGun(NamespacedKey key) {
		this.key = key;

		GunsHandler.getInstance().registerGun(this);

		addActionHandler(GunsHandler.ACTION_SHOOT, this::shoot);
		addActionHandler(GunsHandler.ACTION_HIT, this::hit);
		addActionHandler(GunsHandler.ACTION_RECHARGE, this::recharge);
	}

	public void addAttachment(Attachment attachment) {
		this.attachments.add(attachment);
	}

	public void removeAttachment(Attachment attachment) {
		this.attachments.remove(attachment);
	}

	public Ammunition getFirstFittingAmmunition() {
		return null;
	}

	public int getAmmunitionCharged(ItemStack stack) {
		return 0;
	}

	public void setAmmunitionCharged(ItemStack stack, int amount) {

	}

	public Ammunition getAmmunitionTypeCharged(ItemStack stack) {
		return null;
	}

	public void setAmmunitionTypeCharged(ItemStack stack, Ammunition ammunition) {

	}

	public int getAmmunitionUncharged(Player player, Ammunition ammunition) {
		return 0;
	}

	public void addAmmunition(Ammunition ammunition) {
		this.ammunition.add(ammunition);
	}

	public void removeAmmunition(Ammunition ammunition) {
		this.ammunition.remove(ammunition);
	}

	public Vector getRecoil(Vector direction) {
		return direction.multiply(-.1);
	}

	@Override
	public <C extends GunActionContext> void addActionHandler(GunAction<C> action, ContextConsumer<C> handler) {
		actionMap.put(action, handler);
	}

	public <C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable {
		ContextConsumer<C> handler = (ContextConsumer<C>) actionMap.get(action);
		if (handler == null) {
			return;
		}
		handler.accept(context);
	}

	public void hit(HitContext context) {

	}

	public void recharge(RechargeContext context) {

		Ammunition ammunition = getFirstFittingAmmunition();
		EffectPlayer effectPlayer = getRechargeEffectFactory().get();

		for (Attachment attachment : attachments) {
			try {
				attachment.perform(GunsHandler.ACTION_RECHARGE, context);
			} catch (Throwable t) {
				GunsHandler.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Could not perform guns attachment action for gun '" + key.toString() + "'.", t);
			}
		}
		if (context.getCancellable().isCancelled()) {
			return;
		}
		context.getAmmunition().recharge(this);
		effectPlayer.play(context.getPlayer().getLocation());
	}

	public void shoot(ShootContext context) {

		context.getCancellable().setCancelled(true);

		Ammunition ammunition = getAmmunitionTypeCharged(context.getStack());
		// No ammunition charged
		if (ammunition == null) {
			noAmmunitionEffectFactory.get().play(context.getPlayer().getEyeLocation());
			return;
		}

		// Prepare shot
		EffectPlayer flash = getMuzzleFlashFactory().get();
		GunProjectile projectile = ammunition.getProjectile();
		projectile.setVelocity(context.getPlayer().getLocation().getDirection().normalize().multiply(10));

		context.setMuzzleFlash(flash);
		context.setProjectile(projectile);
		context.setRecoil(getRecoil(context.getPlayer().getLocation().getDirection()));
		context.setAmmunitionCosts(1);

		// Process
		for (Attachment attachment : attachments) {
			try {
				attachment.perform(GunsHandler.ACTION_SHOOT, context);
			} catch (Throwable t) {
				GunsHandler.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Could not perform guns attachment action for gun '" + key.toString() + "'.", t);
			}
		}
		if (context.isCancelled()) {
			noAmmunitionEffectFactory.get().play(context.getPlayer().getEyeLocation());
			return;
		}

		Player player = context.getPlayer();

		// Not enough ammunition charged
		if (ammunition.removeCount(player, context.getAmmunitionCosts())) {
			return;
		}

		// Apply shot
		player.setVelocity(player.getVelocity().add(context.getRecoil()));
		flash.play(player.getEyeLocation());
		projectile.create(player);
	}

	public void updateWeaponStack(ItemStack stack) {

	}

	public ItemStack createWeaponStack() {
		ItemStack stack = itemStack.clone();
		ItemMeta meta = stack.getItemMeta();

		if(meta instanceof Damageable damageable) {
			damageable.setDamage(stack.getType().getMaxDurability() - 1);
		}

		Component name = GunsHandler.getInstance().deserializeLine(getName());

		TagResolver resolver = TagResolver.builder()
				.tag("name", Tag.inserting(name))
				/*TODO.tag("ammo_amount", Tag.inserting())
				.tag("ammo_type", Tag.inserting())
				.tag("attachments", Tag.inserting())*/
				.build();
		meta.setDisplayName(GunsHandler.LEGACY_SERIALIZER.serialize(name));
		meta.setLore(getLore().stream()
				.map(s -> GunsHandler.getInstance().deserializeLine(s, resolver))
				.map(GunsHandler.LEGACY_SERIALIZER::serialize)
				.toList());
		stack.setItemMeta(meta);

		return GunsHandler.getInstance().addGunTag(stack, this);
	}
}
