package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.*;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

@Getter
@Setter
public class SimpleGun implements Gun {

	private final NamespacedKey key;
	private ItemStack itemStack;
	private ComponentLike name;
	private List<ComponentLike> lore;

	private List<Attachment> attachments;
	private List<Ammunition> ammunition;

	public SimpleGun(NamespacedKey key) {
		this.key = key;
	}

	public void addAttachment(Attachment attachment) {
		this.attachments.add(attachment);
	}

	public void removeAttachment(Attachment attachment) {
		this.attachments.remove(attachment);
	}

	public int getAmmunitionCharged(Player player) {
		return 0;
	}

	public Ammunition getAmmunitionTypeCharged(Player player) {
		return null;
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

	public void perform(Player player) {

	}

	public void recharge(Player player) {

	}

	public void shoot(Player player) {

		MuzzleFlash flash = new SimpleMuzzleFlash();
		Projectile projectile = new SimpleProjectile();

		ShootContext context = new ShootContext(player, flash, projectile, getRecoil(player.getLocation().getDirection()));

		for (Attachment attachment : attachments) {
			attachment.shoot(context);
		}

		if(context.isCancelled()) {
			return;
		}

		flash.play(player.getLocation());
		projectile.create(player.getLocation());
	}
}
