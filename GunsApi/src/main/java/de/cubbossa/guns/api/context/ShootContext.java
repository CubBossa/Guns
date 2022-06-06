package de.cubbossa.guns.api.context;

import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.GunProjectile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Getter
@Setter
public class ShootContext extends GunActionContext {

	private EffectPlayer muzzleFlash;
	private ProjectileContext projectile;
	private Vector recoil;
	private int ammunitionCosts;

	public ShootContext(Player player, ItemStack itemStack, Cancellable cancellable) {
		super(player, itemStack, cancellable);
	}
}
