package de.cubbossa.guns.api;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;

public interface Impact<T> {

	int handleHit(Projectile cause, T target, Location hitLocation);
}
