package de.cubbossa.guns.api.impact;

import de.cubbossa.guns.api.Impact;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Projectile;

public class BlockImpact implements Impact<Block> {

	@Override
	public int handleHit(Projectile cause, Block target, Location hitLocation) {
		return 0;
	}
}
