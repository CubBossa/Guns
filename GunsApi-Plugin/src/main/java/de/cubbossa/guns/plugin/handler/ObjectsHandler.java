package de.cubbossa.guns.plugin.handler;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.effects.*;
import de.cubbossa.guns.plugin.GunsAPI;
import de.cubbossa.guns.plugin.SerializableGun;
import de.cubbossa.nbo.bukkit.NBOBukkitSerializer;
import lombok.Getter;
import nbo.NBOFile;
import nbo.NBOSerializer;
import org.bukkit.Effect;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Projectile;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Getter
public class ObjectsHandler {

	private final NBOSerializer serializer = new NBOSerializer();

	@Getter
	private static ObjectsHandler instance;

	private final Map<String, Object> objectRegistry;
	private final Map<String, EffectPlayer> effectRegistry;
	private final Map<String, Ammunition> attachmentRegistry;
	private final Map<String, Ammunition> ammunitionRegistry;
	private final Map<String, Projectile> projectileRegistry;
	private final Map<String, Gun> gunsRegistry;
	//TODO impact

	public ObjectsHandler() {
		instance = this;
		this.objectRegistry = new HashMap<>();
		this.effectRegistry = new HashMap<>();
		this.attachmentRegistry = new HashMap<>();
		this.ammunitionRegistry = new HashMap<>();
		this.projectileRegistry = new HashMap<>();
		this.gunsRegistry = new HashMap<>();
	}

	public void loadFile() {
		objectRegistry.clear();
		effectRegistry.clear();
		attachmentRegistry.clear();
		ammunitionRegistry.clear();
		projectileRegistry.clear();
		gunsRegistry.clear();
		try {
			NBOFile file = NBOFile.loadFile(new File(GunsAPI.getInstance().getDataFolder(), "guns.nbo"), serializer);
			objectRegistry.putAll(file.getObjectMap());
		} catch (Throwable t) {
			GunsAPI.getInstance().getLogger().log(Level.SEVERE, "Error while reading guns.nbo: ", t);
		}
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
		NBOBukkitSerializer.addBukkitSerialization(serializer);

		serializer
				.register(EffectPlayer.class, EffectPlayer::deserialize, EffectPlayer::serialize)
				.register(SoundPlayer.class, SoundPlayer::deserialize, SoundPlayer::serialize)
				.register(ParticlePlayer.class, ParticlePlayer::deserialize, ParticlePlayer::serialize)
				.register(ParticleLinePlayer.class, ParticleLinePlayer::deserialize, ParticleLinePlayer::serialize)
				.register(WorldEffectPlayer.class, WorldEffectPlayer::deserialize, WorldEffectPlayer::serialize)
				.register(SerializableGun.class, SerializableGun::deserialize, SerializableGun::serialize);
	}
}
