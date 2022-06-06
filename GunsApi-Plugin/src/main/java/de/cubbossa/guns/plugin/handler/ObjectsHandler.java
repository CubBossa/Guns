package de.cubbossa.guns.plugin.handler;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.Impact;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.effects.*;
import de.cubbossa.guns.api.impact.EntityImpact;
import de.cubbossa.guns.plugin.*;
import de.cubbossa.nbo.bukkit.NBOBukkitSerializer;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import nbo.LinkedHashMapBuilder;
import nbo.NBOFile;
import nbo.NBOSerializer;
import org.apache.commons.lang.SerializationException;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

@Getter
public class ObjectsHandler {

	private final NBOSerializer serializer = NBOFile.DEFAULT_SERIALIZER;

	@Getter
	private static ObjectsHandler instance;

	private final Map<String, Object> objectRegistry;
	private final Map<String, EffectPlayer> effectRegistry;
	private final Map<String, Projectile> projectileRegistry;
	private final Map<String, Impact<?>> impactRegistry;

	public ObjectsHandler() {
		instance = this;
		this.objectRegistry = new HashMap<>();
		this.effectRegistry = new HashMap<>();
		this.projectileRegistry = new HashMap<>();
		this.impactRegistry = new HashMap<>();
		NBOBukkitSerializer.addBukkitSerialization(serializer);
	}

	public void loadFile() {
		GunsHandler gunsHandler = GunsHandler.getInstance();
		gunsHandler.getAmmoRegistry().clear();
		gunsHandler.getAttachmentRegistry().clear();
		gunsHandler.getGunsRegistry().clear();

		objectRegistry.clear();
		effectRegistry.clear();
		projectileRegistry.clear();
		impactRegistry.clear();

		try {
			NBOFile file = NBOFile.loadFile(new File(GunsAPI.getInstance().getDataFolder(), "guns.nbo"), serializer);
			objectRegistry.putAll(file.getReferenceObjects());
		} catch (Throwable t) {
			GunsAPI.getInstance().getLogger().log(Level.SEVERE, "Error while reading guns.nbo: ", t);
		}
		objectRegistry.forEach((string, o) -> {
			if (o instanceof EffectPlayer effectPlayer) {
				effectRegistry.put(string, effectPlayer);
			} else if (o instanceof Attachment attachment) {
				gunsHandler.getAttachmentRegistry().put(attachment);
			} else if (o instanceof Ammunition ammunition) {
				gunsHandler.getAmmoRegistry().put(ammunition);
			} else if (o instanceof Projectile projectile) {
				projectileRegistry.put(string, projectile);
			} else if (o instanceof Gun gun) {
				gunsHandler.getGunsRegistry().put(gun);
			} else if (o instanceof Impact impact) {
				impactRegistry.put(string, impact);
			}
		});
	}

	public void registerDefaults() {
		NBOBukkitSerializer.addBukkitSerialization(serializer);
		serializer
				.registerMapSerializer(EffectPlayer.class, EffectPlayer::deserialize, EffectPlayer::serialize)
				.registerMapSerializer(SoundPlayer.class, SoundPlayer::deserialize, SoundPlayer::serialize)
				.registerMapSerializer(ParticlePlayer.class, ParticlePlayer::deserialize, ParticlePlayer::serialize)
				.registerMapSerializer(ParticleLinePlayer.class, ParticleLinePlayer::deserialize, ParticleLinePlayer::serialize)
				.registerMapSerializer(WorldEffectPlayer.class, WorldEffectPlayer::deserialize, WorldEffectPlayer::serialize)
				.registerMapSerializer(SerializableProjectile.class, SerializableProjectile::deserialize, SerializableProjectile::serialize)
				.registerMapSerializer(SerializableAmmunition.class, SerializableAmmunition::deserialize, SerializableAmmunition::serialize)
				.registerMapSerializer(SerializableGun.class, SerializableGun::deserialize, SerializableGun::serialize)
				.registerMapSerializer(EntityImpact.class, EntityImpact::deserialize, EntityImpact::serialize)
				.registerMapSerializer(ItemStack.class, map -> {
					if (map.containsKey("id") && map.get("id") instanceof String type) {
						int count = 1;
						if (map.containsKey("Count") && (map.get("Count") instanceof Short || map.get("Count") instanceof Integer)) {
							count = (int) map.get("Count");
						}
						NamespacedKey key = NamespacedKey.fromString(type);
						if (key == null) {
							throw new SerializationException("Could not deserialize ItemStack.class, id was invalid: " + type);
						}
						Material material = Registry.MATERIAL.get(key);
						if (material == null) {
							throw new SerializationException("Could not deserialize ItemStack.class. Unknown material: " + key.toString());
						}
						String nbt = "{}";
						Object tagMap = map.get("tag");
						if (tagMap != null) {
							new NBOSerializer().convertObjectToAst(tagMap, new NBOFile()).toNBTString();
						}
						ItemStack stack = new ItemStack(material, count);
						NBTItem nbtItem = new NBTItem(stack);
						nbtItem.mergeCompound(new NBTContainer(nbt));
						return nbtItem.getItem();
					}
					throw new SerializationException("Could not deserialize ItemStack.class. Material 'id' is required but missing.");
				}, stack -> new LinkedHashMapBuilder<String, Object>().put("id", stack.getType().getKey()).put("Count:", stack.getAmount()).put("tag", "{}").build())

				.registerMapSerializer(EntityFactory.class, map -> {
					if (map.containsKey("id") && map.get("id") instanceof String type) {
						NamespacedKey key = NamespacedKey.fromString(type);
						if (key == null) {
							throw new SerializationException("Could not deserialize EntityFactory.class, id was invalid: " + type);
						}
						EntityType entityType = Registry.ENTITY_TYPE.get(key);
						if (entityType == null) {
							throw new SerializationException("Could not deserialize EntityFactory.class. Unknown EntityType: " + key.toString());
						}
						return new EntityFactory(entityType, map, new NBTContainer(new NBOSerializer().convertObjectToAst(map, new NBOFile()).toNBTString()));
					}
					throw new SerializationException("Could not deserialize EntityFactory.class. EntityType 'id' is required but missing.");
				}, EntityFactory::getObjectRepresentation);
	}
}
