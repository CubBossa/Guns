package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.GunProjectile;
import de.cubbossa.guns.api.Impact;
import lombok.Getter;
import lombok.Setter;
import nbo.NBOSerializable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SerializableProjectile implements GunProjectile, NBOSerializable {

	private EntityType projectileType = EntityType.SNOWBALL;
	private ItemStack displayItem = new ItemStack(Material.POLISHED_BLACKSTONE_BUTTON);
	private Vector velocity = new Vector(1, 0, 0);
	private float accuracy = 0;
	private final List<Impact<?>> impacts;

	public SerializableProjectile() {
		impacts = new ArrayList<>();
	}

	@Override
	public <T> void addImpact(Impact<T> impact) {
		impacts.add(impact);
	}

	@Override
	public <T> void removeImpact(Impact<T> impact) {
		impacts.remove(impact);
	}

	public void setProjectileType(EntityType projectileType) {
		this.projectileType = projectileType;
	}

	@Override
	public void create(Player player) {
		if (projectileType == null) {
			throw new RuntimeException("No projectile type provided!");
		}
		if (projectileType.getEntityClass().isAssignableFrom(Projectile.class)) {
			Class<? extends Projectile> c = (Class<? extends Projectile>) projectileType.getEntityClass();
			Projectile projectile = player.launchProjectile(c, velocity);
			if (projectile instanceof Snowball snowball) {
				snowball.setItem(displayItem);
			}
		} else {
			Entity projectile = player.getLocation().getWorld().spawnEntity(player.getLocation(), projectileType);
			projectile.setVelocity(velocity);
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("entityType", projectileType.getKey().toString());
		map.put("velocity", velocity);
		if (projectileType.equals(EntityType.SNOWBALL)) {
			map.put("displayItem", displayItem);
		}
		if (!impacts.isEmpty()) {
			map.put("impactActions", impacts);
		}
		map.put("accuracy", accuracy);
		return map;
	}

	public static SerializableProjectile deserialize(Map<String, Object> map) {
		SerializableProjectile projectile = new SerializableProjectile();
		if (map.containsKey("entityType")) {
			Object o = map.get("entityType");
			if (o instanceof String string) {
				projectile.setProjectileType(Registry.ENTITY_TYPE.get(NamespacedKey.fromString(string)));
			}
		}
		if (map.containsKey("velocity")) {
			Object o = map.get("velocity");
			if (o instanceof Vector vector) {
				projectile.setVelocity(vector);
			}
		}
		if (map.containsKey("displayItem")) {
			Object o = map.get("displayItem");
			if (o instanceof ItemStack displayItem) {
				projectile.setDisplayItem(displayItem);
			}
		}
		if (map.containsKey("accuracy")) {
			Object o = map.get("accuracy");
			if (o instanceof Float aFloat) {
				projectile.setAccuracy(aFloat);
			}
		}
		if (map.containsKey("impactActions")) {
			Object o = map.get("impactActions");
			if (o instanceof List impacts) {
				projectile.impacts.addAll(impacts);
			}
		}
		return projectile;
	}
}
