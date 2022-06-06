package de.cubbossa.guns.api.impact;

import de.cubbossa.guns.api.Impact;
import de.cubbossa.guns.api.effects.EffectPlayer;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class EntityImpact implements Impact<Entity> {

	private int damage = 0;
	private Vector velocity = new Vector(0, 0, 0);
	private EffectPlayer effectPlayer;
	private final ArrayList<PotionEffect> effects;

	public EntityImpact() {
		effects = new ArrayList<>();
	}

	@Override
	public int handleHit(Projectile cause, Entity target, Location hitLocation) {
		if (target instanceof LivingEntity entity) {
			for (PotionEffect effect : effects) {
				effect.apply(entity);
			}
		}
		if (!target.isDead()) {
			target.setVelocity(velocity);
		}
		effectPlayer.play(hitLocation);
		return damage;
	}

	public Map<String, Object> serialize() {
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		map.put("damage", damage);
		map.put("velocity", velocity);
		map.put("impact_effect", effectPlayer);
		map.put("potion_effects", effects);
		return map;
	}

	public static EntityImpact deserialize(Map<String, Object> map) {
		EntityImpact impact = new EntityImpact();
		if (map.containsKey("damage") && map.get("damage") instanceof Integer damage) {
			impact.setDamage(damage);
		}
		if (map.containsKey("velocity") && map.get("velocity") instanceof Vector velocity) {
			impact.setVelocity(velocity);
		}
		if (map.containsKey("impact_effect") && map.get("impact_effect") instanceof EffectPlayer effectPlayer) {
			impact.setEffectPlayer(effectPlayer);
		}
		if (map.containsKey("potion_effects") && map.get("potion_effects") instanceof List<?> list) {
			impact.effects.addAll(list.stream().filter(o -> o instanceof PotionEffect).map(o -> (PotionEffect) o).collect(Collectors.toList()));
		}
		return impact;
	}
}
