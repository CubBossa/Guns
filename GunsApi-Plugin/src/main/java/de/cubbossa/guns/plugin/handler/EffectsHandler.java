package de.cubbossa.guns.plugin.handler;

import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import lombok.Getter;
import org.apache.commons.lang.SerializationException;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

	public void loadEffectPlayersFromFile(File file) {
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		for (String key : cfg.getKeys(false)) {

			EffectPlayer effectPlayer = new EffectPlayer();

			ConfigurationSection durationSection = cfg.getConfigurationSection(key);
			for (String durations : durationSection.getKeys(false)) {
				if (!Pattern.matches("delay_[0-9]+[st]]", durations)) {
					return;
				}
				int duration = Integer.parseInt(durations.substring(6, durations.length() - 1));
				duration *= durations.charAt(durations.length() - 1) == 's' ? 20 : 1;

				for (String effectString : durationSection.getStringList(durations)) {
					try {
						effectPlayer.addEffect(duration, deserialize(effectString));
					} catch (ParseException e) {
						//TODO debug
					}
				}
			}
			registerEffect(key, effectPlayer);
		}
	}

	public void saveEffectToFile(File file, EffectPlayer effectPlayer, String key) {
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		Map<EffectPlayer, Integer> players = effectPlayer.getEffectPlayers(true);
		for (int amount : players.values().stream().distinct().sorted().toList()) {
			cfg.set("delay_" + amount + "t", players.entrySet().stream()
					.filter(e -> e.getValue() == amount)
					.map(Map.Entry::getKey)
					.map(this::serialize)
					.collect(Collectors.toList()));
		}
	}

	public String serialize(EffectPlayer effectPlayer) {
		if (effectPlayer instanceof SoundPlayer) {
			return serializeSoundPlayer((SoundPlayer) effectPlayer);
		}
		throw new SerializationException("Could not serialize EffectPlayer of type " + effectPlayer.getClass().getName());
	}

	public EffectPlayer deserialize(String effectString) throws ParseException {
		if (effectString.startsWith("Effect:")) {
			return getEffect(effectString.substring(7));
		}

		Pattern pattern = Pattern.compile("([a-zA-Z]+)(\\[([a-zA-Z]+=(('[a-zA-Z0-9]+')|[a-zA-Z0-9]+),)*[a-zA-Z]+=(('[a-zA-Z0-9]+')|[a-zA-Z0-9]+)])?");
		Matcher matcher = pattern.matcher(effectString);
		if (!matcher.matches()) {
			throw new ParseException("Could not parse effect: " + effectString, 0);
		}
		String type = matcher.group(1);

		if (matcher.group(2) == null) {
			return new EffectPlayer();
		}
		String params = matcher.group(2);
		params = params.substring(1, params.length() - 1);
		if(params.startsWith("'")) {
			params = params.substring(1, params.length() - 1);
		}

		Map<String, String> paramMap = Arrays.stream(params.split(","))
				.map(string -> string.split("="))
				.collect(Collectors.toMap(x -> x[0], x -> x[1]));
		return switch (type) {
			case "SoundPlayer" -> deserializeSoundPlayer(paramMap);
			default -> new EffectPlayer();
		};
	}

	public SoundPlayer deserializeSoundPlayer(Map<String, String> args) {
		SoundPlayer soundPlayer = new SoundPlayer();
		String soundString = args.get("sound");
		if (soundString != null) {
			soundPlayer.setSound(Sound.valueOf(soundString));
		}
		String volumeString = args.get("volume");
		if (volumeString != null) {
			soundPlayer.setVolume(Float.parseFloat(volumeString));
		}
		String pitchString = args.get("pitch");
		if (pitchString != null) {
			soundPlayer.setPitch(Float.parseFloat(pitchString));
		}
		return soundPlayer;
	}

	public String serializeSoundPlayer(SoundPlayer soundPlayer) {
		return "SoundPlayer[sound='" + soundPlayer.getSound().getKey() + "',volume=" + soundPlayer.getVolume() + ",pitch=" + soundPlayer.getPitch() + "]";
	}
}
