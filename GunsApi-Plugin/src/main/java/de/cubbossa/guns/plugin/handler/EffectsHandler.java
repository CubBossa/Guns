package de.cubbossa.guns.plugin.handler;

import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import lombok.Getter;
import nbo.NBOFile;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConfigurationOptions;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class EffectsHandler {

	@Getter
	private static EffectsHandler instance;

	private final Map<String, EffectPlayer> effectRegistry;

	public EffectsHandler() {
		instance = this;
		this.effectRegistry = new HashMap<>();
	}

	public void registerEffect(String key, EffectPlayer effectPlayer) {
		if (effectRegistry.containsKey(key)) {
			throw new RuntimeException("An effect with key '" + key + "' already exists.");
		}
		effectRegistry.put(key, effectPlayer);
	}

	public @Nullable EffectPlayer getEffect(String key) {
		return effectRegistry.getOrDefault(key, null);
	}

	public void registerDefaults() {
		//ConfigurationSerialization.
		NBOFile.register(ItemStack.class, ItemStack::deserialize, ItemStack::serialize);

		NBOFile.register(SoundPlayer.class, stringObjectMap -> {
			return new SoundPlayer((Sound) stringObjectMap.getOrDefault("sound", Sound.ENTITY_VILLAGER_NO),
					(Float) stringObjectMap.getOrDefault("volume", 1),
					(Float) stringObjectMap.getOrDefault("pitch", 1));
		}, soundPlayer -> {
			return Map.of("sound", soundPlayer.getSound().getKey().toString(), "volume", soundPlayer.getVolume(),
					"pitch", soundPlayer.getPitch());
		});

	}
}
