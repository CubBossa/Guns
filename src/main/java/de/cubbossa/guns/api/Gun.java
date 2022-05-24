package de.cubbossa.guns.api;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

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

	int getAmmunitionCharged(Player player);

	Ammunition getAmmunitionTypeCharged(Player player);

	int getAmmunitionUncharged(Player player, Ammunition ammunition);

	void addAmmunition(Ammunition ammunition);

	void removeAmmunition(Ammunition ammunition);

	Vector getRecoil(Vector direction);

	void perform(Player player);

	void recharge(Player player);

	void shoot(Player player);
}
