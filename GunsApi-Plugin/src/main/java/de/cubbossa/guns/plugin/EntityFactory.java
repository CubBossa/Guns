package de.cubbossa.guns.plugin;

import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.Map;
import java.util.function.Function;

@Getter
@Setter
@AllArgsConstructor
public class EntityFactory {

	private EntityType entityType;
	private Map<String, Object> objectRepresentation;
	private NBTContainer nbtContainer;

	public Entity spawnEntity(Location location, Function<Location, Entity> spawnFunction) {
		Entity entity = spawnFunction.apply(location);
		NBTEntity nbtEntity = new NBTEntity(entity);
		nbtEntity.mergeCompound(nbtContainer);
		return entity;
	}
}
