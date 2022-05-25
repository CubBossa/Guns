package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.*;
import de.cubbossa.guns.api.context.RechargeContext;
import de.cubbossa.guns.api.context.ShootContext;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Supplier;

@Getter
@Setter
public class SimpleGun implements Gun {

	private final NamespacedKey key;
	private ItemStack itemStack;
	private ComponentLike name;
	private List<ComponentLike> lore;

	private List<Attachment> attachments;
	private List<Ammunition> ammunition;
	private Supplier<Projectile> projectileFactory;
	private Supplier<EffectPlayer> muzzleFlashFactory;
	private Supplier<EffectPlayer> rechargeEffectFactory;

	public SimpleGun(NamespacedKey key) {
		this.key = key;
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

	public void hit(Player player) {

	}

	public void recharge(Player player) {

		Ammunition ammunition = getFirstFittingAmmunition();

		EffectPlayer effectPlayer = getRechargeEffectFactory().get();
		RechargeContext context = new RechargeContext(player, ammunition);

		for(Attachment attachment : attachments) {
			attachment.recharge(context);
		}
		if(context.isCancelled()) {
			return;
		}
		context.getAmmunition().recharge(this);
		effectPlayer.play(player.getLocation());
	}

	public void shoot(Player player) {

		EffectPlayer flash = getMuzzleFlashFactory().get();
		Projectile projectile = getProjectileFactory().get();

		ShootContext context = new ShootContext(player, flash, projectile, getRecoil(player.getLocation().getDirection()));

		// Process
		for (Attachment attachment : attachments) {
			attachment.shoot(context);
		}

		if(context.isCancelled()) {
			return;
		}
		if(!ammunition.isEmpty()) {
			Ammunition ammo = getAmmunitionTypeCharged(player);
			// Weapon not charged
			if(ammo == null) {
				return;
			}
			// Not enough ammo charged
			if(ammo.removeCount(player, context.getAmmunitionCosts())) {
				return;
			}
		}

		flash.play(player.getLocation());
		projectile.create(player.getLocation());
	}

	public ItemStack createWeaponStack() {
		ItemStack stack = itemStack.clone();
		ItemMeta meta = stack.getItemMeta();
		//meta.setDisplayName(x);
		//meta.setLore(x);
		meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);
		stack.setItemMeta(meta);
		return stack;
	}
}
