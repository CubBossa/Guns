package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.GunProjectile;
import lombok.Getter;
import lombok.Setter;
import nbo.NBOSerializable;
import net.kyori.adventure.text.ComponentLike;
import org.apache.commons.lang.SerializationException;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class SerializableAmmunition implements Ammunition, NBOSerializable {

	private final NamespacedKey key;
	private String nameFormat;
	private int bulletCount = 16;
	private GunProjectile projectile;
	private ItemStack ammoStack;
	private Recipe recipe;

	public SerializableAmmunition(NamespacedKey key) {
		this.key = key;
	}

	@Override
	public ComponentLike getName() {
		return GunsAPI.getInstance().getMiniMessage().deserialize(nameFormat);
	}

	@Override
	public int getCount(Player player) {
		return ammoStack == null ? 0 : (int) Arrays.stream(player.getInventory().getContents()).filter(i -> i.isSimilar(ammoStack)).count();
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key", key.toString());
		map.put("name", nameFormat);
		map.put("magazine-stack", ammoStack);
		map.put("bullet-count", bulletCount);
		map.put("crafting-recipe", recipe);
		map.put("projectile", projectile);
		return map;
	}

	public static SerializableAmmunition deserialize(Map<String, Object> map) {
		if (!map.containsKey("key") || !(map.get("key") instanceof String)) {
			throw new SerializationException("SerializableAmmunition requires a 'key' attribute.");
		}
		NamespacedKey key = NamespacedKey.fromString((String) map.get("key"));
		if (key == null) {
			throw new SerializationException("Key for SerializableAmmunition could not be read.");
		}
		SerializableAmmunition ammo = new SerializableAmmunition(key);
		if (map.containsKey("name") && map.get("name") instanceof String name) {
			ammo.setNameFormat(name);
		}
		if (map.containsKey("magazine-stack") && map.get("magazine-stack") instanceof ItemStack stack) {
			ammo.setAmmoStack(stack);
		}
		if (map.containsKey("bullet-count") && map.get("bullet-count") instanceof Integer size) {
			ammo.setBulletCount(size);
		}
		if (map.containsKey("crafting-recipe") && map.get("crafting-recipe") instanceof Recipe recipe) {
			ammo.setRecipe(recipe);
		}
		if (map.containsKey("projectile") && map.get("projectile") instanceof GunProjectile projectile) {
			ammo.setProjectile(projectile);
		}
		return ammo;
	}
}
