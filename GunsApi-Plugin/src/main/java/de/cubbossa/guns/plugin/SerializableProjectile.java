package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.GunListener;
import de.cubbossa.guns.api.GunProjectile;
import de.cubbossa.guns.api.Impact;
import de.cubbossa.guns.api.TrailsHandler;
import de.cubbossa.guns.api.context.ProjectileContext;
import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.impact.BlockImpact;
import de.cubbossa.guns.api.impact.EntityImpact;
import lombok.Getter;
import lombok.Setter;
import nbo.NBOSerializable;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class SerializableProjectile implements GunProjectile, NBOSerializable {

	private EntityFactory entityFactory;
	private Vector velocity = new Vector(1, 1, 1);
	private float accuracy = 0;
	private Impact<Entity> entityImpact;
	private Impact<Block> blockImpact;
	private EffectPlayer trailEffect;
	private int trailTicks = 10;

	@Override
	public void create(ProjectileContext context) {

		Player player = context.getPlayer();
		Entity entity = entityFactory.spawnEntity(player.getLocation(), (entityType, location) -> {
			if (Projectile.class.isAssignableFrom(entityType.getEntityClass())) {
				Class<? extends Projectile> c = (Class<? extends Projectile>) entityType.getEntityClass();
				return player.launchProjectile(c, context.getVelocity());
			} else {
				Entity e = player.getLocation().getWorld().spawnEntity(player.getEyeLocation(), entityType);
				e.setVelocity(context.getVelocity());
				return e;
			}
		});

		if (entityImpact != null) {
			GunListener.getInstance().entityImpacts.put(entity.getUniqueId(), entityImpact);
		}
		if (blockImpact != null) {
			GunListener.getInstance().blockImpacts.put(entity.getUniqueId(), blockImpact);
		}
		if (trailEffect != null) {
			TrailsHandler.getInstance().addTrail(entity, trailTicks, trailEffect);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("entity-factory", entityFactory);
		map.put("velocity", velocity);
		map.put("entity-impact", entityImpact);
		map.put("block-impact", blockImpact);
		map.put("accuracy", accuracy);
		map.put("trail-effect", trailEffect);
		map.put("trail-ticks", trailTicks);
		return map;
	}

	public static SerializableProjectile deserialize(Map<String, Object> map) {
		SerializableProjectile projectile = new SerializableProjectile();
		if (map.containsKey("entity-factory") && map.get("entity-factory") instanceof EntityFactory factory) {
			projectile.setEntityFactory(factory);
		}
		if (map.containsKey("velocity") && map.get("velocity") instanceof Vector vector) {
			projectile.setVelocity(vector);
		}
		if (map.containsKey("accuracy") && map.get("accuracy") instanceof Float aFloat) {
			projectile.setAccuracy(aFloat);
		}
		if (map.containsKey("entity-impact") && map.get("entity-impact") instanceof EntityImpact impact) {
			projectile.entityImpact = impact;
		}
		if (map.containsKey("block-impact") && map.get("block-impact") instanceof BlockImpact impact) {
			projectile.blockImpact = impact;
		}
		if (map.containsKey("trail-effect") && map.get("trail-effect") instanceof EffectPlayer effectPlayer) {
			projectile.trailEffect = effectPlayer;
		}
		if (map.containsKey("trail-ticks") && map.get("trail-ticks") instanceof Integer ticks) {
			projectile.trailTicks = ticks;
		}
		return projectile;
	}
}
