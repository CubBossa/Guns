package de.cubbossa.guns.api.context;

import de.cubbossa.guns.api.EffectPlayer;
import de.cubbossa.guns.api.Projectile;
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
	private final EffectPlayer muzzleFlash;
	private final Projectile projectile;
	private Vector recoil;
	private int ammunitionCosts = 1;
	private boolean cancelled = false;

	public ShootContext(Player player, EffectPlayer muzzleFlash, Projectile projectile, Vector recoil) {
		this.player = player;
		this.muzzleFlash = muzzleFlash;
		this.projectile = projectile;
		this.recoil = recoil;
	}
}
