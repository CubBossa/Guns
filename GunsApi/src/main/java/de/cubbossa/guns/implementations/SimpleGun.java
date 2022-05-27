package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.*;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.context.*;
import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import lombok.Getter;
import lombok.Setter;
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
	private List<Ammunition> validAmmunition = new ArrayList<>();
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
		return getValidAmmunition().get(0);
	}

	public Map.Entry<Ammunition, Integer> getAmmunitionCharged(ItemStack stack) {
		return GunsHandler.getInstance().getAmmunition(stack);
	}

	public void setAmmunitionCharged(ItemStack stack, Ammunition ammunition, int amount) {
		GunsHandler.getInstance().setAmmunition(stack, ammunition, amount);
	}

	public int getAmmunitionUncharged(Player player, Ammunition ammunition) {
		return ammunition.getCount(player);
	}

	public void addValidAmmunition(Ammunition ammunition) {
		this.validAmmunition.add(ammunition);
	}

	public void removeValidAmmunition(Ammunition ammunition) {
		this.validAmmunition.remove(ammunition);
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

		EffectPlayer effectPlayer = getRechargeEffectFactory().get();
		context.setAmmunition(getFirstFittingAmmunition());

		context.getCancellable().setCancelled(true);
		for (Attachment attachment : attachments) {
			try {
				attachment.perform(GunsHandler.ACTION_RECHARGE, context);
			} catch (Throwable t) {
				GunsHandler.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Could not perform guns attachment action for gun '" + key.toString() + "'.", t);
			}
		}
		if (context.isCancelled()) {
			return;
		}

		GunsHandler.getInstance().setAmmunition(context.getStack(), context.getAmmunition(), context.getAmmunition().getMagazineCount());
		GunsHandler.getInstance().updateItemStack(context.getStack(), this, context.getAmmunition(), context.getAmmunition().getMagazineCount());
		effectPlayer.play(context.getPlayer().getLocation());
	}

	public void shoot(ShootContext context) {

		context.getCancellable().setCancelled(true);

		var pair = getAmmunitionCharged(context.getStack());
		// No ammunition charged
		if (pair == null) {
			noAmmunitionEffectFactory.get().play(context.getPlayer().getEyeLocation());
			return;
		}
		Ammunition ammunition = pair.getKey();

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
			return;
		}

		Player player = context.getPlayer();

		// Not enough ammunition charged
		if (pair.getValue() < context.getAmmunitionCosts()) {
			noAmmunitionEffectFactory.get().play(context.getPlayer().getEyeLocation());
			return;
		}
		GunsHandler.getInstance().setAmmunition(context.getStack(), ammunition, pair.getValue() - context.getAmmunitionCosts());
		GunsHandler.getInstance().updateItemStack(context.getStack(), this, ammunition, pair.getValue() - context.getAmmunitionCosts());
		player.getInventory().setItemInMainHand(context.getStack());

		// Apply shot
		player.setVelocity(player.getVelocity().add(context.getRecoil()));
		flash.play(player.getEyeLocation());
		projectile.create(player);
	}

	public void updateWeaponStack(ItemStack stack) {
		Map.Entry<Ammunition, Integer> pair = getAmmunitionCharged(stack);
		if (pair == null) {
			return;
		}
		GunsHandler.getInstance().updateItemStack(stack, this, pair.getKey(), pair.getValue());
	}

	public ItemStack createWeaponStack() {
		ItemStack stack = itemStack.clone();
		ItemMeta meta = stack.getItemMeta();

		if (meta instanceof Damageable damageable) {
			damageable.setDamage(stack.getType().getMaxDurability() - 1);
		}
		meta.setDisplayName(GunsHandler.LEGACY_SERIALIZER.serialize(GunsHandler.getInstance().deserializeLine(getName())));
		meta.setLore(GunsHandler.getInstance().getLore(this));
		stack.setItemMeta(meta);

		return GunsHandler.getInstance().setIdentifier(stack, this);
	}
}
