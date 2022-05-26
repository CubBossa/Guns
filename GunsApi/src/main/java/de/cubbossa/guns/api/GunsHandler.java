package de.cubbossa.guns.api;

import de.cubbossa.guns.api.context.GunActionContext;
import de.cubbossa.guns.api.context.HitContext;
import de.cubbossa.guns.api.context.RechargeContext;
import de.cubbossa.guns.api.context.ShootContext;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GunsHandler {

	public static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacySection();
	public static GunAction<ShootContext> ACTION_SHOOT;
	public static GunAction<HitContext> ACTION_HIT;
	public static GunAction<RechargeContext> ACTION_RECHARGE;

	@Getter
	private static GunsHandler instance;

	@Getter
	private JavaPlugin plugin;
	public NamespacedKey gunMetaKey;
	public NamespacedKey gunChargedAmmoType;
	public NamespacedKey gunChargedAmmoCount;
	private final Map<NamespacedKey, Gun> gunsRegistry;

	public GunsHandler(JavaPlugin plugin) {
		instance = this;
		this.plugin = plugin;
		gunMetaKey = new NamespacedKey(plugin, "gunType");
		gunChargedAmmoType = new NamespacedKey(plugin, "gunChargedAmmoType");
		gunChargedAmmoCount = new NamespacedKey(plugin, "gunChargedAmmoCount");
		gunsRegistry = new HashMap<>();

		ACTION_SHOOT = createAction(new NamespacedKey(plugin, "simple_shoot"));
		ACTION_HIT = createAction(new NamespacedKey(plugin, "simple_hit"));
		ACTION_RECHARGE = createAction(new NamespacedKey(plugin, "simple_recharge"));
	}

	public <C extends GunActionContext> GunAction<C> createAction(NamespacedKey key) {
		return new GunAction<>(key);
	}

	public @Nullable Gun getGun(NamespacedKey key) {
		return gunsRegistry.getOrDefault(key, null);
	}

	public void registerGun(Gun gun) {
		if (gunsRegistry.containsKey(gun.getKey())) {
			throw new IllegalArgumentException("A gun with key '" + gun.getKey().toString() + "' already exists.");
		}
		gunsRegistry.put(gun.getKey(), gun);
	}

	public boolean isGun(ItemStack stack) {
		if(stack == null ||stack.getType() == Material.AIR) {
			return false;
		}
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return false;
		}
		return meta.getPersistentDataContainer().has(gunMetaKey, PersistentDataType.STRING);
	}

	public @Nullable Gun getGun(ItemStack stack) {
		if(stack == null ||stack.getType() == Material.AIR) {
			return null;
		}
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return null;
		}
		String keyString = meta.getPersistentDataContainer().get(gunMetaKey, PersistentDataType.STRING);
		if (keyString == null) {
			return null;
		}
		NamespacedKey key = NamespacedKey.fromString(keyString);
		if (key == null) {
			return null;
		}
		return gunsRegistry.getOrDefault(key, null);
	}

	public ItemStack addGunTag(ItemStack itemStack, Gun gun) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		}
		if (meta == null) {
			throw new RuntimeException("Could not create GunItem for gun '" + gun.getKey() + "', meta is null.");
		}
		meta.getPersistentDataContainer().set(gunMetaKey, PersistentDataType.STRING, gun.getKey().toString());
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public void addGunTag(Gun gun, ItemStack itemStack, Ammunition type, int amount) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		}
		if (meta == null) {
			throw new RuntimeException("Could not create GunItem for gun '" + gun.getKey() + "', meta is null.");
		}
		meta.getPersistentDataContainer().set(gunChargedAmmoType, PersistentDataType.STRING, type.getKey().toString());
		meta.getPersistentDataContainer().set(gunChargedAmmoCount, PersistentDataType.INTEGER, amount);
		itemStack.setItemMeta(meta);
	}

	public <C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable {
		Gun gun = getGun(context.getStack());
		// Item is not a gun
		if (gun == null) {
			return;
		}

		Player player = context.getPlayer();
		// Player not allowed to use gun
		if (!gun.getUsePredicate().test(player)) {
			return;
		}

		// Let gun handle
		gun.perform(action, context);
	}

	public Component deserializeLine(String text, TagResolver... resolver) {
		return MiniMessage.miniMessage().deserialize(text, resolver);
	}

	public List<Component> deserializeLines(String text, TagResolver... resolver) {
		MiniMessage mm = MiniMessage.miniMessage();
		return Arrays.stream(text.split("\n")).map(s -> mm.deserialize(s, resolver)).collect(Collectors.toList());
	}
}
