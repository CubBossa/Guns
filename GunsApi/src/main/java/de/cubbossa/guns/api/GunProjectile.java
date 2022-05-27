package de.cubbossa.guns.api;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface GunProjectile {

	Vector getVelocity();

	void setVelocity(Vector velocity);

	ItemStack getDisplayItem();

	void setDisplayItem(ItemStack displayItem);

	float getAccuracy();

	void setAccuracy(float accuracy);

	void create(Player player);
}
