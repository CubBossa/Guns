package de.cubbossa.guns.api;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public interface Projectile {

	Vector getVelocity();

	void setVelocity(Vector velocity);

	ItemStack getDisplayItem();

	void setDisplayItem(ItemStack displayItem);

	float getAccuracy();

	void setAccuracy(float accuracy);

	int getParticleType();

	void setParticleType();

	int getParticlePeriod();

	void setParticlePeriod();

	void create(Location location);
}
