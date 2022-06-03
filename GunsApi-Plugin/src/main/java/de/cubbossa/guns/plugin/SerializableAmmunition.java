package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.GunProjectile;
import lombok.Getter;
import lombok.Setter;
import nbo.NBOSerializable;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class SerializableAmmunition implements Ammunition, NBOSerializable {

	private final NamespacedKey key;
	private String nameFormat;
	private int magazineSize = 16;
	private GunProjectile projectile;

	public SerializableAmmunition(NamespacedKey key) {
		this.key = key;
	}

	@Override
	public ComponentLike getName() {
		return GunsAPI.getInstance().getMiniMessage().deserialize(nameFormat);
	}

	@Override
	public int getCount(Player player) {
		return 200;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key", key.toString());
		map.put("name", nameFormat);
		map.put("magazineSize", magazineSize);
		map.put("projectile", projectile);
		return map;
	}

	public static SerializableAmmunition deserialize(Map<String, Object> map) {
		if (!map.containsKey("key") || !(map.get("key") instanceof String)) {
			throw new RuntimeException("SerializableAmmunition requires a 'key' attribute.");
		}
		NamespacedKey key = NamespacedKey.fromString((String) map.get("key"));
		if (key == null) {
			throw new RuntimeException("Key for SerializableAmmunition could not be read.");
		}
		SerializableAmmunition ammo = new SerializableAmmunition(key);
		if (map.containsKey("name") && map.get("name") instanceof String name) {
			ammo.setNameFormat(name);
		}
		if (map.containsKey("magazineSize") && map.get("magazineSize") instanceof Integer size) {
			ammo.setMagazineSize(size);
		}
		if (map.containsKey("projectile") && map.get("projectile") instanceof GunProjectile projectile) {
			ammo.setProjectile(projectile);
		}
		return ammo;
	}
}
