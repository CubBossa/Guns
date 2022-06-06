package de.cubbossa.guns.api.context;

import de.cubbossa.guns.api.GunProjectile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@Getter
@Setter
public class ProjectileContext {

	private final GunProjectile projectile;
	private final Player player;
	private Vector velocity;

	public ProjectileContext(GunProjectile projectile, Player player) {
		this.projectile = projectile;
		this.player = player;
		this.velocity = projectile.getVelocity().clone().multiply(player.getLocation().getDirection());
	}
}
