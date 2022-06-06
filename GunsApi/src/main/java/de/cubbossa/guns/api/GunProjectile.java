package de.cubbossa.guns.api;

import de.cubbossa.guns.api.context.ProjectileContext;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface GunProjectile {

	Vector getVelocity();

	void setVelocity(Vector velocity);

	float getAccuracy();

	void setAccuracy(float accuracy);

	Impact<Entity> getEntityImpact();

	Impact<Block> getBlockImpact();

	void setEntityImpact(Impact<Entity> entityImpact);

	void setBlockImpact(Impact<Block> entityImpact);

	void create(ProjectileContext context);
}
