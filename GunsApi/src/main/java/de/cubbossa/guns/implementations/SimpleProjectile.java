package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.GunProjectile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

@Getter
@Setter
public class SimpleProjectile implements GunProjectile {

	private Vector velocity = new Vector(0, 1, 0);
	private ItemStack displayItem = new ItemStack(Material.POLISHED_BLACKSTONE_BUTTON);
	private float accuracy = 0;
	private Particle particleType;
	private int particlePeriod;

	public void create(Player player) {
		Snowball projectile = player.launchProjectile(Snowball.class);
		projectile.setItem(displayItem != null ? displayItem.clone() : new ItemStack(Material.AIR));
	}
}
