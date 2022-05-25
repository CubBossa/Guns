package de.cubbossa.guns.api;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Supplier;

public interface Gun {

	NamespacedKey getKey();

	ItemStack getItemStack();

	void setItemStack(ItemStack stack);

	ComponentLike getName();

	void setName(ComponentLike name);

	List<ComponentLike> getLore();

	void setLore(List<ComponentLike> lore);

	List<Attachment> getAttachments();

	void addAttachment(Attachment attachment);

	void removeAttachment(Attachment attachment);

	List<Ammunition> getAmmunition();

	Ammunition getFirstFittingAmmunition();

	int getAmmunitionCharged(Player player);

	void setAmmunitionCharged(Player player, int amount);

	Ammunition getAmmunitionTypeCharged(Player player);

	void setAmmunitionTypeCharged(Player player, Ammunition ammunition);

	int getAmmunitionUncharged(Player player, Ammunition ammunition);

	void addAmmunition(Ammunition ammunition);

	void removeAmmunition(Ammunition ammunition);

	Supplier<EffectPlayer> getMuzzleFlashFactory();

	void setMuzzleFlashFactory(Supplier<EffectPlayer> factory);

	Supplier<Projectile> getProjectileFactory();

	void setProjectileFactory(Supplier<Projectile> factory);

	Supplier<EffectPlayer> getRechargeEffectFactory();

	void getRechargeEffectFactory(Supplier<EffectPlayer> effect);

	Vector getRecoil(Vector direction);

	void hit(Player player);

	void recharge(Player player);

	void shoot(Player player);

	ItemStack createWeaponStack();
}
