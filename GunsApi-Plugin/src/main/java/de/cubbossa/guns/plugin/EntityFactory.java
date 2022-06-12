package de.cubbossa.guns.plugin;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTCompoundList;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.SerializationException;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class EntityFactory {

	private EntityType entityType;
	private Map<String, Object> objectRepresentation;
	private NBTContainer nbtContainer;

	private BiFunction<EntityType, Location, Entity> spawnFunction;
	private Location location;

	public EntityFactory(EntityType entityType, Map<String, Object> objectRepresentation, NBTContainer nbtContainer) {
		this.entityType = entityType;
		this.objectRepresentation = objectRepresentation;
		this.nbtContainer = nbtContainer;
	}

	public Entity spawnEntity(Location location, BiFunction<EntityType, Location, Entity> spawnFunction) {
		this.location = location;
		this.spawnFunction = spawnFunction;
		return fromNBT(nbtContainer);
	}

	/**
	 * Parses the nbt string for an entity into actual entities.
	 * If the nbt string contains any passengers, it first spawns every passenger and then attaches them to the entity.
	 * <p>
	 * e.g. {id:"minecraft:donkey",Passengers:[{id:"minecraft:wolf",Passengers:[{id:"minecraft:cat"}]}]}
	 */
	private Entity fromNBT(NBTCompound nbtContainer) {
		String keyString = nbtContainer.getString("id");
		if (keyString == null) {
			throw new SerializationException("Could not summon entity, no 'id' attribute provided.");
		}
		NamespacedKey key = NamespacedKey.fromString(keyString);
		if (key == null) {
			throw new SerializationException("Could not summon entity, invalid 'id' attribute: " + keyString);
		}
		EntityType type = Registry.ENTITY_TYPE.get(key);

		NBTCompoundList passengersCompound = nbtContainer.getCompoundList("Passengers");
		List<Entity> passengers = passengersCompound.stream().map(this::fromNBT).collect(Collectors.toList());
		Entity entity = spawnFunction.apply(type, location);
		NBTEntity nbtEntity = new NBTEntity(entity);
		nbtEntity.mergeCompound(nbtContainer);
		passengers.forEach(entity::addPassenger);
		return entity;
	}
}
