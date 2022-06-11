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
import nbo.exception.NBOParseException;
import nbo.exception.NBOReferenceException;
import org.apache.commons.lang.SerializationException;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class ObjectsHandler {

	private final NBOSerializer serializer = NBOFile.DEFAULT_SERIALIZER;


	@Getter
	private static ObjectsHandler instance;

	private final Map<NamespacedKey, Recipe> customRecipeRegistry;
	private final Map<String, Object> objectRegistry;
	private final Map<String, EffectPlayer> effectRegistry;
	private final Map<String, Projectile> projectileRegistry;
	private final Map<String, Impact<?>> impactRegistry;

	public ObjectsHandler() {
		instance = this;
		this.customRecipeRegistry = new HashMap<>();
		this.objectRegistry = new HashMap<>();
		this.effectRegistry = new HashMap<>();
		this.projectileRegistry = new HashMap<>();
		this.impactRegistry = new HashMap<>();
		NBOBukkitSerializer.addBukkitSerialization(serializer);
	}

	public void loadFile() throws NBOReferenceException, IOException, NBOParseException, ClassNotFoundException {
		GunsHandler gunsHandler = GunsHandler.getInstance();
		gunsHandler.getAmmoRegistry().clear();
		gunsHandler.getAttachmentRegistry().clear();
		gunsHandler.getGunsRegistry().clear();

		objectRegistry.clear();
		effectRegistry.clear();
		projectileRegistry.clear();
		impactRegistry.clear();

		NBOFile file = NBOFile.loadFile(new File(GunsAPI.getInstance().getDataFolder(), "guns.nbo"), serializer);
		objectRegistry.putAll(file.getReferenceObjects());

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
				}, stack -> new LinkedHashMapBuilder<String, Object>()
						.put("id", stack.getType().getKey().toString())
						.put("Count", stack.getAmount())
						.put("tag", new NBTItem(stack).toString()).build())

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
				}, EntityFactory::getObjectRepresentation)

				.registerMapSerializer(ShapelessRecipe.class, map -> {
					if (map.containsKey("id") && map.get("id") instanceof String type) {
						NamespacedKey key = NamespacedKey.fromString(type);
						if (key == null) {
							throw new SerializationException("Could not deserialize ShapelessRecipe.class, id was invalid: " + type);
						}
						if (!map.containsKey("result") || !(map.get("result") instanceof ItemStack)) {
							throw new SerializationException("Could not deserialize ShapelessRecipe.class, result missing.");
						}

						ShapelessRecipe recipe = new ShapelessRecipe(key, (ItemStack) map.get("result"));
						if (map.containsKey("ingredients") && map.get("ingredients") instanceof List<?> list) {
							for (Object o : list) {
								if (o instanceof ItemStack stack) {
									recipe.addIngredient(new RecipeChoice.ExactChoice(stack));
								} else if (o instanceof List<?> l) {
									recipe.addIngredient(new RecipeChoice.ExactChoice(l.stream()
											.filter(o1 -> o1 instanceof ItemStack)
											.map(o1 -> (ItemStack) o1)
											.collect(Collectors.toList())));
									recipe.addIngredient(new RecipeChoice.MaterialChoice(l.stream()
											.filter(o1 -> o1 instanceof String)
											.map(o1 -> NamespacedKey.fromString((String) o1))
											.filter(Objects::nonNull)
											.map(Registry.MATERIAL::get)
											.collect(Collectors.toList())));
								} else if (o instanceof String string) {
									NamespacedKey k = NamespacedKey.fromString(string);
									if (k != null) {
										Material mat = Registry.MATERIAL.get(k);
										if (mat != null) {
											recipe.addIngredient(new RecipeChoice.MaterialChoice(mat));
										}
									}
								}
							}
						}
						if (map.containsKey("group") && map.get("group") instanceof String string) {
							recipe.setGroup(string);
						}
						return recipe;
					}
					throw new SerializationException("Could not deserialize Recipe.class.'id' is required but missing.");
				}, shapelessRecipe -> new LinkedHashMapBuilder<String, Object>()
						.put("id", shapelessRecipe.getKey().toString())
						.put("group", shapelessRecipe.getGroup())
						.put("ingredients", shapelessRecipe.getIngredientList())
						.put("result", shapelessRecipe.getResult())
						.build());
	}
}
