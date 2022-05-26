package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.*;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.context.*;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
	private ComponentLike name = Component.empty();
	private List<ComponentLike> lore = new ArrayList<>();
	private Predicate<Player> usePredicate = p -> true;

	private List<Attachment> attachments = new ArrayList<>();
	private List<Ammunition> ammunition = new ArrayList<>();
	private Supplier<GunProjectile> projectileFactory = SimpleProjectile::new;
	private Supplier<EffectPlayer> muzzleFlashFactory = () -> new SoundPlayer().withSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF);
	private Supplier<EffectPlayer> rechargeEffectFactory = EffectPlayer::new;
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

	public int getAmmunitionCharged(Player player) {
		return 0;
	}

	public void setAmmunitionCharged(Player player, int amount) {

	}

	public Ammunition getAmmunitionTypeCharged(Player player) {
		return null;
	}

	public void setAmmunitionTypeCharged(Player player, Ammunition ammunition) {

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

		EffectPlayer flash = getMuzzleFlashFactory().get();
		GunProjectile projectile = getProjectileFactory().get();
		projectile.setVelocity(context.getPlayer().getLocation().getDirection().multiply(5));

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
			context.getCancellable().setCancelled(true);
			return;
		}

		Player player = context.getPlayer();

		if (!ammunition.isEmpty()) {
			Ammunition ammo = getAmmunitionTypeCharged(player);
			// Weapon not charged
			if (ammo == null) {
				return;
			}
			// Not enough ammo charged
			if (ammo.removeCount(player, context.getAmmunitionCosts())) {
				return;
			}
		}
		context.getCancellable().setCancelled(true);

		flash.play(player.getLocation());
		projectile.create(player);
	}

	public ItemStack createWeaponStack() {
		ItemStack stack = itemStack.clone();
		ItemMeta meta = stack.getItemMeta();
		//meta.setDisplayName(x);
		//meta.setLore(x);
		stack.setItemMeta(meta);
		return GunsHandler.getInstance().addGunTag(stack, this);
	}
}
