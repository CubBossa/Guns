package de.cubbossa.guns.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
@RequiredArgsConstructor
public class ShootContext {

	private final Player player;
	private final MuzzleFlash muzzleFlash;
	private final Projectile projectile;
	private final Vector recoil;
	private boolean cancelled = false;
}
