package de.cubbossa.guns.plugin.editor;

import de.cubbossa.guns.plugin.GunsAPI;
import de.cubbossa.menuframework.util.DurationParser;
import de.tr7zw.nbtapi.NBTItem;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@UtilityClass
public class TextUtils {

	private static final GsonComponentSerializer GSON_SERIALZIER = GsonComponentSerializer.builder().build();

	private static final LegacyComponentSerializer LEGACY_SERIALIZER_AMPERSAND = LegacyComponentSerializer.builder()
			.character('&')
			.hexColors()
			.hexCharacter('#')
			.build();

	private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.builder()
			.character('§')
			.hexColors()
			.hexCharacter('x')
			.useUnusualXRepeatedCharacterHexFormat()
			.build();

	private static final PlainTextComponentSerializer PLAIN_SERIALIZER = PlainTextComponentSerializer.builder().build();

	public static final String DURATION_FORMAT = new DurationParser(true).format(0);
	public static final String DURATION_FORMAT_REGEX = ""; //TODO

	public static final String DATE_TIME_FORMAT_SHORT = "dd.MM.yy HH:mm";
	public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
	public static final DateTimeFormatter DATE_TIME_FORMATTER_SHORT = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT_SHORT);
	public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

	/**
	 * Format: §x§1§2§3§4§5§6
	 */
	public Component fromLegacy(String legacy) {
		return LEGACY_SERIALIZER.deserialize(legacy);
	}

	/**
	 * Format: &#123456
	 */
	public Component fromChatLegacy(String legacy) {
		return LEGACY_SERIALIZER_AMPERSAND.deserialize(legacy);
	}

	public String toLegacy(Component component) {
		return LEGACY_SERIALIZER.serialize(component);
	}

	public String toPlain(Component component) {
		return PLAIN_SERIALIZER.serialize(component);
	}

	public String toGson(Component component) {
		return GSON_SERIALZIER.serialize(component);
	}

	public String toLegacyFromMiniMessage(String minimessage) {
		return toLegacy(GunsAPI.getInstance().getMiniMessage().deserialize(minimessage));
	}

	public String formatDuration(Duration duration) {
		return new DurationParser().format(duration);
	}

	public Duration parseDuration(String input) {
		return new DurationParser().parse(input);
	}

	public String formatLocalDateTime(@Nullable LocalDateTime localDateTime) {
		if (localDateTime == null) {
			return "-";
		}
		return localDateTime.format(DATE_TIME_FORMATTER_SHORT);
	}

	public @Nullable LocalDateTime parseLocalDateTime(String string) {
		LocalDateTime result = null;
		try {
			result = LocalDateTime.parse(string, DATE_TIME_FORMATTER_SHORT);
		} catch (DateTimeParseException e) {
			try {
				result = LocalDateTime.parse(string, DATE_TIME_FORMATTER);
			} catch (DateTimeParseException ignored) {
			}
		}
		return result;
	}

	/**
	 * @param itemStack the itemstack to display as a text component with hover text.
	 * @return the displayname of the itemstack with the nbt data as hover text.
	 */
	public Component toComponent(ItemStack itemStack, boolean hover) {
		if (!hover) {
			return itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName() ?
					fromLegacy(itemStack.getItemMeta().getDisplayName()) :
					toTranslatable(itemStack.getType());
		}
		return (itemStack.getItemMeta() != null && itemStack.getItemMeta().hasDisplayName() ?
				fromLegacy(itemStack.getItemMeta().getDisplayName()) :
				toTranslatable(itemStack.getType()))
				.hoverEvent(HoverEvent.showItem(HoverEvent.ShowItem.of(Key.key(itemStack.getType().getKey().toString()),
						1, BinaryTagHolder.binaryTagHolder(new NBTItem(itemStack).toString()))));
	}

	public Component toComponent(ItemStack itemStack) {
		return toComponent(itemStack, true);
	}

	public Component toTranslatable(Material material) {
		if(material.isBlock()) {
			return Component.translatable("block.minecraft." + String.valueOf(material).toLowerCase());
		}
		return Component.translatable("item.minecraft." + String.valueOf(material).toLowerCase());
	}
}