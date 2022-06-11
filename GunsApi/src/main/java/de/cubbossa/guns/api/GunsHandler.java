package de.cubbossa.guns.api;

import de.cubbossa.guns.api.attachments.Attachment;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Getter
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
	public NamespacedKey gunAttachments;
	private final HashedRegistry<Gun> gunsRegistry;
	private final HashedRegistry<Attachment> attachmentRegistry;
	private final HashedRegistry<Ammunition> ammoRegistry;

	public GunsHandler(JavaPlugin plugin) {
		instance = this;

		this.plugin = plugin;
		gunMetaKey = new NamespacedKey(plugin, "gunType");
		gunChargedAmmoType = new NamespacedKey(plugin, "gunChargedAmmoType");
		gunChargedAmmoCount = new NamespacedKey(plugin, "gunChargedAmmoCount");
		gunAttachments = new NamespacedKey(plugin, "gunAttachments");

		gunsRegistry = new HashedRegistry<>();
		attachmentRegistry = new HashedRegistry<>();
		ammoRegistry = new HashedRegistry<>();

		ACTION_SHOOT = createAction(new NamespacedKey(plugin, "simple_shoot"));
		ACTION_HIT = createAction(new NamespacedKey(plugin, "simple_hit"));
		ACTION_RECHARGE = createAction(new NamespacedKey(plugin, "simple_recharge"));
	}

	public <C extends GunActionContext> GunAction<C> createAction(NamespacedKey key) {
		return new GunAction<>(key);
	}

	public boolean isGun(ItemStack stack) {
		if (stack == null || stack.getType() == Material.AIR) {
			return false;
		}
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return false;
		}
		return meta.getPersistentDataContainer().has(gunMetaKey, PersistentDataType.STRING);
	}

	public @Nullable Gun getGun(ItemStack stack) {
		if(stack == null || stack.getType() == Material.AIR) {
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

	public ItemStack setIdentifier(ItemStack itemStack, Gun gun) {
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

	public void setAmmunition(ItemStack itemStack, @Nullable Ammunition type, int amount) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		}
		if (meta == null) {
			throw new RuntimeException("Could not edit GunItem, meta is null.");
		}
		meta.getPersistentDataContainer().set(gunChargedAmmoType, PersistentDataType.STRING, type == null ? "none" : type.getKey().toString());
		meta.getPersistentDataContainer().set(gunChargedAmmoCount, PersistentDataType.INTEGER, amount);
		itemStack.setItemMeta(meta);
	}

	public List<String> getLore(Gun gun) {
		TagResolver resolver = TagResolver.builder()
				/*TODO.tag("ammo_amount", Tag.inserting())
				.tag("ammo_type", Tag.inserting())
				.tag("attachments", Tag.inserting())*/
				.build();

		return gun.getLore().stream()
				.map(s -> GunsHandler.getInstance().deserializeLine(s, resolver))
				.map(GunsHandler.LEGACY_SERIALIZER::serialize)
				.toList();
	}

	public void updateItemStack(ItemStack stack, Gun gun, @Nullable Ammunition type, int amount) {
		float percent = type == null ? 0 : amount / (float) type.getBulletCount();
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		}
		if (meta == null) {
			throw new RuntimeException("Could not edit GunItem, meta is null.");
		}
		if (meta instanceof Damageable damageable) {
			damageable.setDamage((int) ((1 - percent) * stack.getType().getMaxDurability()));
		}
		meta.setLore(getLore(gun));
		stack.setItemMeta(meta);
	}

	public @Nullable Map.Entry<Ammunition, Integer> getAmmunition(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			return null;
		}
		String key = meta.getPersistentDataContainer().get(gunChargedAmmoType, PersistentDataType.STRING);
		Integer amount = meta.getPersistentDataContainer().get(gunChargedAmmoCount, PersistentDataType.INTEGER);
		if (key == null || amount == null || key.equals("none")) {
			return null;
		}
		Ammunition ammunition = ammoRegistry.get(NamespacedKey.fromString(key));
		if (ammunition == null) {
			return null;
		}
		return new AbstractMap.SimpleEntry<>(ammunition, amount);
	}

	public <C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable {

		// Player not allowed to use gun
		if (!context.getGun().getUsePredicate().test(context.getPlayer())) {
			return;
		}

		// Let gun handle
		context.getGun().perform(action, context);
	}

	public Component deserializeLine(String text, TagResolver... resolver) {
		return MiniMessage.miniMessage().deserialize(text, resolver);
	}

	public List<Component> deserializeLines(String text, TagResolver... resolver) {
		MiniMessage mm = MiniMessage.miniMessage();
		return Arrays.stream(text.split("\n")).map(s -> mm.deserialize(s, resolver)).collect(Collectors.toList());
	}

	public void addAttachment(ItemStack stack, Attachment attachment) {
		ItemMeta meta = getMeta(stack);
		String attachments = meta.getPersistentDataContainer().get(gunAttachments, PersistentDataType.STRING);
		if (attachments == null || attachments.isEmpty()) {
			attachments = attachment.getKey().toString();
		} else {
			attachments += "," + attachment.getKey().toString();
		}
		meta.getPersistentDataContainer().set(gunAttachments, PersistentDataType.STRING, attachments);
		stack.setItemMeta(meta);
	}

	public void removeAttachment(ItemStack stack, Attachment attachment) {
		ItemMeta meta = getMeta(stack);
		String attachments = meta.getPersistentDataContainer().get(gunAttachments, PersistentDataType.STRING);
		if (attachments == null || attachments.isEmpty()) {
			return;
		}
		if (attachments.contains("," + attachment.getKey().toString())) {
			attachments = attachments.replace("," + attachment.getKey().toString(), "");
		} else if (attachments.contains(attachment.getKey().toString())) {
			attachments = attachments.replace(attachment.getKey().toString(), "");
		}
		meta.getPersistentDataContainer().set(gunAttachments, PersistentDataType.STRING, attachments);
		stack.setItemMeta(meta);
	}

	public void setAttachments(ItemStack stack, List<Attachment> attachments) {
		ItemMeta meta = getMeta(stack);
		meta.getPersistentDataContainer().set(gunAttachments, PersistentDataType.STRING, attachments.stream()
				.map(a -> a.getKey().toString()).collect(Collectors.joining(",")));
		stack.setItemMeta(meta);
	}

	public List<Attachment> getAttachments(ItemStack stack) {
		ItemMeta meta = getMeta(stack);
		String list = meta.getPersistentDataContainer().get(gunAttachments, PersistentDataType.STRING);
		if (list == null) {
			return new ArrayList<>();
		}
		return Arrays.stream(list.split(",")).map(NamespacedKey::fromString).filter(Objects::nonNull).map(attachmentRegistry::get).collect(Collectors.toList());
	}

	private ItemMeta getMeta(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(stack.getType());
		}
		if (meta == null) {
			throw new RuntimeException("Could not modify stack, meta was null.");
		}
		return meta;
	}
}
