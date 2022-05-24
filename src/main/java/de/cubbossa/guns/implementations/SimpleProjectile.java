package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.Projectile;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SimpleProjectile implements Projectile {

	@Override public Vector getVelocity() {
		return null;
	}

	@Override public void setVelocity(Vector velocity) {

	}

	@Override public ItemStack getDisplayItem() {
		return null;
	}

	@Override public void setDisplayItem(ItemStack displayItem) {

	}

	@Override public float getAccuracy() {
		return 0;
	}

	@Override public void setAccuracy(float accuracy) {

	}

	@Override public int getParticleType() {
		return 0;
	}

	@Override public void setParticleType() {

	}

	@Override public int getParticlePeriod() {
		return 0;
	}

	@Override public void setParticlePeriod() {

	}

	@Override public void create(Location location) {

	}
}
