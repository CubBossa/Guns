package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.GunProjectile;
import de.cubbossa.guns.plugin.handler.ObjectsHandler;
import lombok.Getter;
import lombok.Setter;
import nbo.NBOSerializable;
import net.kyori.adventure.text.ComponentLike;
import org.apache.commons.lang.SerializationException;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class SerializableAmmunition implements Ammunition, NBOSerializable {

	private final NamespacedKey key;
	private String nameFormat;
	private int bulletCount = 16;
	private GunProjectile projectile;
	private ItemStack ammoStack;
	private List<Recipe> recipe = new ArrayList<>();

	public SerializableAmmunition(NamespacedKey key) {
		this.key = key;
	}

	@Override
	public ComponentLike getName() {
		return GunsAPI.getInstance().getMiniMessage().deserialize(nameFormat);
	}

	@Override
	public int getCount(Player player) {
		return ammoStack == null ? bulletCount : (int) Arrays.stream(player.getInventory().getContents())
				.filter(i -> i != null && i.isSimilar(ammoStack))
				.mapToInt(ItemStack::getAmount)
				.sum();
	}

	public void addCount(Player player, int count) {

	}

	public void removeCount(Player player, int count) {
		int c = count;
		if (ammoStack != null) {
			for (ItemStack stack : player.getInventory()) {
				if (stack == null) {
					continue;
				}
				if (stack.isSimilar(ammoStack)) {
					if (stack.getAmount() >= c) {
						stack.setAmount(stack.getAmount() - c);
						break;
					}
					c -= stack.getAmount();
					stack.setAmount(0);
				}
			}
		}
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", key.toString());
		map.put("name", nameFormat);
		map.put("magazine-stack", ammoStack);
		map.put("bullet-count", bulletCount);
		map.put("crafting-recipes", recipe);
		map.put("projectile", projectile);
		return map;
	}

	public static SerializableAmmunition deserialize(Map<String, Object> map) {
		if (!map.containsKey("id") || !(map.get("id") instanceof String)) {
			throw new SerializationException("SerializableAmmunition requires a 'id' attribute.");
		}
		NamespacedKey key = NamespacedKey.fromString((String) map.get("id"));
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
		if (map.containsKey("crafting-recipes") && map.get("crafting-recipes") instanceof List<?> list) {
			ammo.setRecipe(list.stream().filter(o -> o instanceof Recipe).map(o -> (Recipe) o).collect(Collectors.toList()));
			for (Recipe r : ammo.getRecipe()) {
				ObjectsHandler.getInstance().getCustomRecipeRegistry().put(((Keyed) r).getKey(), r);
			}
		}
		if (map.containsKey("projectile") && map.get("projectile") instanceof GunProjectile projectile) {
			ammo.setProjectile(projectile);
		}
		return ammo;
	}
}
