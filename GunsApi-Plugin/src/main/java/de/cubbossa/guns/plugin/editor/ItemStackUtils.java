package de.cubbossa.guns.plugin.editor;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.cubbossa.guns.plugin.GunsAPI;
import de.cubbossa.menuframework.util.DurationParser;
import de.cubbossa.translations.MenuIcon;
import de.cubbossa.translations.Message;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class ItemStackUtils {

	public static String HEAD_URL_ARROW_NEXT = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";
	public static String HEAD_URL_ARROW_NEXT_OFF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOGFhMTg3ZmVkZTg4ZGUwMDJjYmQ5MzA1NzVlYjdiYTQ4ZDNiMWEwNmQ5NjFiZGM1MzU4MDA3NTBhZjc2NDkyNiJ9fX0=";
	public static String HEAD_URL_ARROW_PREV = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
	public static String HEAD_URL_ARROW_PREV_OFF = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjZkYWI3MjcxZjRmZjA0ZDU0NDAyMTkwNjdhMTA5YjVjMGMxZDFlMDFlYzYwMmMwMDIwNDc2ZjdlYjYxMjE4MCJ9fX0=";

	public static String HEAD_URL_ARROW_UP = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzA0MGZlODM2YTZjMmZiZDJjN2E5YzhlYzZiZTUxNzRmZGRmMWFjMjBmNTVlMzY2MTU2ZmE1ZjcxMmUxMCJ9fX0=";
	public static String HEAD_URL_ARROW_DOWN = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzQzNzM0NmQ4YmRhNzhkNTI1ZDE5ZjU0MGE5NWU0ZTc5ZGFlZGE3OTVjYmM1YTEzMjU2MjM2MzEyY2YifX19";

	public static String HEAD_URL_LETTER_CHECK_MARK = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTkyZTMxZmZiNTljOTBhYjA4ZmM5ZGMxZmUyNjgwMjAzNWEzYTQ3YzQyZmVlNjM0MjNiY2RiNDI2MmVjYjliNiJ9fX0=";
	public static String HEAD_URL_LETTER_X = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==";
	public static String HEAD_URL_LETTER_EXCLAMATION = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjYyNDVmYjM5N2I3YzJiM2EzNmUyYTI0ZDQ5NmJlMjU4ZjFjZGY0MTA1NGY5OWU5YzY1ZTFhNjczYWRkN2I0In19fQ==";

	public static Material MATERIAL_DATES = Material.CLOCK;
	public static Material MATERIAL_DURATIONS = Material.COMPASS;
	public static Material MATERIAL_PERMISSIONS = Material.STRUCTURE_VOID;
	public static Material MATERIAL_GUNS = Material.STONE_HOE;
	public static Material MATERIAL_AMMO = Material.IRON_NUGGET;
	public static Material MATERIAL_ATTACH = Material.REDSTONE;
	public static Material MATERIAL_PROJECTILE = Material.FIRE_CHARGE;
	public static Material MATERIAL_EFFECT = Material.CANDLE;
	public static Material MATERIAL_SOUND = Material.NAUTILUS_SHELL;
	public static Material MATERIAL_PARTICLE = Material.ORANGE_DYE;

	public GsonComponentSerializer GSON_SERIALIZER = GsonComponentSerializer.builder().build();
	public LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
			.character('§')
			.hexColors()
			.useUnusualXRepeatedCharacterHexFormat()
			.hexCharacter('x')
			.build();
	public DurationParser DURATION_PARSER = new DurationParser(ChronoUnit.DAYS, ChronoUnit.HOURS, ChronoUnit.MINUTES);

	public void giveOrDrop(Player player, ItemStack itemStack) {
		giveOrDrop(player, itemStack, player.getLocation());
	}

	public void giveOrDrop(Player player, @Nullable ItemStack item, Location location) {

		if (item == null || item.getType() == Material.AIR) {
			return;
		}
		Map<Integer, ItemStack> leftoverItems = player.getInventory().addItem(item.clone());
		if (leftoverItems.isEmpty()) {
			return;
		}
		leftoverItems.forEach((index, item2) -> location.getWorld().dropItemNaturally(location, item2));
	}

	public ItemStack addLore(ItemStack itemStack, List<Component> lore) {
		NBTItem item = new NBTItem(itemStack);
		NBTCompound display = item.getCompound("display");
		if (display == null) {
			display = item.addCompound("display");
		}
		List<String> presentLore = display.getStringList("Lore");
		presentLore.addAll(lore.stream().map(component -> {
			return component.decoration(TextDecoration.ITALIC, component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET ?
					TextDecoration.State.FALSE : component.decoration(TextDecoration.ITALIC));
		}).map(component -> GSON_SERIALIZER.serialize(component)).collect(Collectors.toList()));
		return item.getItem();
	}

	public ItemStack setDisplayName(ItemStack stack, ComponentLike name) {
		Component n = name.asComponent();
		NBTItem item = new NBTItem(stack);
		NBTCompound display = item.getCompound("display");
		if (display == null) {
			display = item.addCompound("display");
		}
		TextDecoration.State decoration = n.decoration(TextDecoration.ITALIC);
		display.setString("Name", GSON_SERIALIZER.serialize(n.decoration(TextDecoration.ITALIC,
				decoration.equals(TextDecoration.State.NOT_SET) ? TextDecoration.State.FALSE : decoration)));
		return item.getItem();
	}

	public ItemStack setLore(ItemStack itemStack, List<? extends ComponentLike> lore) {
		NBTItem item = new NBTItem(itemStack);
		NBTCompound display = item.getCompound("display");
		if (display == null) {
			display = item.addCompound("display");
		}
		List<String> presentLore = display.getStringList("Lore");
		presentLore.clear();
		presentLore.addAll(lore.stream().map(ComponentLike::asComponent).map(component -> {
			return component.decoration(TextDecoration.ITALIC, component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET ?
					TextDecoration.State.FALSE : component.decoration(TextDecoration.ITALIC));
		}).map(component -> GSON_SERIALIZER.serialize(component)).collect(Collectors.toList()));
		return item.getItem();
	}

	public ItemStack setCustomModelData(ItemStack itemStack, int customModelData) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		}
		meta.setCustomModelData(customModelData);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public ItemStack createButtonItemStack(boolean val, Message name, Message lore) {
		return ItemStackUtils.createItemStack(val ? Material.LIME_DYE : Material.GRAY_DYE,
				name.asComponent(TagResolver.resolver("value", Tag.inserting(Component.text(val)))),
				lore.asComponents(TagResolver.resolver("value", Tag.inserting(Component.text(val)))));
	}

	public ItemStack createItemStack(Material material, int customModelData) {
		return ItemStackUtils.setCustomModelData(new ItemStack(material), customModelData);
	}


	public ItemStack createItemStack(Material material, String displayName, @Nullable String lore) {
		if (lore != null) {
			List<String> loreList = Lists.newArrayList(lore.split("\n"));
			return createItemStack(material, displayName, loreList);
		}
		return createItemStack(material, displayName, (List<String>) null);
	}

	public ItemStack createItemStack(Material material, String displayName, @Nullable List<String> lore) {

		ItemStack itemStack = new ItemStack(material);
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(material);
			if (meta == null) {
				throw new RuntimeException("Could not create item stack, meta was null.");
			}
		}
		meta.setDisplayName(displayName);
		if (lore != null && !lore.isEmpty() && (lore.size() > 1 || !lore.get(0).isEmpty() || !lore.get(0).isBlank())) {
			meta.setLore(lore);
		}
		meta.addItemFlags(ItemFlag.values());
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public ItemStack createItemStack(Material material, ComponentLike displayName, List<? extends ComponentLike> lore) {
		List<String> stringLore = lore.stream().map(component -> SERIALIZER.serialize(component.asComponent())).collect(Collectors.toList());
		return createItemStack(material, SERIALIZER.serialize(displayName.asComponent()), stringLore);
	}

	public ItemStack createItemStack(Material material, Message name, Message lore) {
		return createItemStack(material, name, lore.asComponents());
	}

	public static ItemStack createItemStack(ItemStack itemStack, Message name, Message lore) {
		return createItemStack(itemStack, name, lore.asComponents());
	}

	public static ItemStack createItemStack(ItemStack itemStack, ComponentLike name, List<? extends ComponentLike> lore) {
		ItemMeta meta = itemStack.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
		}
		meta.setDisplayName(TextUtils.toLegacy(name.asComponent()));
		meta.setLore(lore.stream().map(ComponentLike::asComponent).map(TextUtils::toLegacy).collect(Collectors.toList()));
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public ItemStack createCustomHead(String url) {
		return createCustomHead(new ItemStack(Material.PLAYER_HEAD, 1), url);
	}

	public ItemStack createCustomHead(String url, Message name, Message lore) {
		return createCustomHead(createItemStack(Material.PLAYER_HEAD, name, lore), url);
	}

	public ItemStack createCustomHead(ItemStack itemStack, String url) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta instanceof SkullMeta meta) {
			GameProfile profile = new GameProfile(UUID.randomUUID(), null);
			profile.getProperties().put("textures", new Property("textures", url));

			try {
				Field profileField = meta.getClass().getDeclaredField("profile");
				profileField.setAccessible(true);
				profileField.set(meta, profile);

			} catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
				error.printStackTrace();
			}
			itemStack.setItemMeta(meta);
		} else {
			throw new UnsupportedOperationException("Trying to add a skull texture to a non-playerhead item");
		}
		return itemStack;
	}

	public ItemStack createErrorItem(Message errorName, Message errorDescription, TagResolver error) {
		ItemStack stack = Icon.STACK_WARNING_RP.clone();
		return new MenuIcon.Builder(stack).withName(errorName).withLore(errorDescription)
				.withLoreResolver(error).build().createItem();
	}

	public ItemStack createInfoItem(Message name, Message lore) {
		ItemStack stack = new MenuIcon(new ItemStack(Material.PAPER, 1), name, lore).createItem();
		stack = setCustomModelData(stack, 7121000);
		return stack;
	}

	public ItemStack setFlags(ItemStack stack) {
		ItemMeta meta = stack.getItemMeta();
		meta.addItemFlags(ItemFlag.values());
		stack.setItemMeta(meta);
		return stack;
	}

	public ItemStack setNameAndLore(ItemStack itemStack, ComponentLike name, List<? extends ComponentLike> lore) {
		itemStack = setDisplayName(itemStack, name);
		itemStack = setLore(itemStack, lore);
		return itemStack;
	}

	public ItemStack setNameAndLore(ItemStack itemStack, Message name, Message lore) {
		itemStack = setDisplayName(itemStack, name);
		itemStack = setLore(itemStack, lore.asComponents());
		return itemStack;
	}

	public ItemStack setNameAndLore(ItemStack item, String displayName, String lore) {
		ItemMeta meta = item.getItemMeta();
		if (meta == null) {
			meta = Bukkit.getItemFactory().getItemMeta(item.getType());
		}
		MiniMessage miniMessage = GunsAPI.getInstance().getMiniMessage();
		meta.setDisplayName(SERIALIZER.serialize(miniMessage.deserialize((displayName))));
		List<String> legacyLore = Arrays.stream(lore.split("\n")).map(s -> SERIALIZER.serialize(miniMessage.deserialize(s))).collect(Collectors.toList());
		meta.setLore(legacyLore);
		item.setItemMeta(meta);
		return item;
	}

	public ItemStack setGlow(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		if (meta != null) {
			meta.addEnchant(Enchantment.LUCK, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
		}
		return item;
	}
}
