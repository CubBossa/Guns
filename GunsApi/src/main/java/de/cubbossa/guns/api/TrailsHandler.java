package de.cubbossa.guns.api;

import de.cubbossa.guns.api.effects.EffectPlayer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

public class TrailsHandler implements Listener {

	public record Trail(Entity entity, EffectPlayer effectPlayer, int ticks) {
	}

	@Getter
	private static TrailsHandler instance;

	private final JavaPlugin plugin;
	private final HashMap<Entity, Integer> entityTickMap = new HashMap<>();
	private final TreeMap<Integer, Collection<Trail>> trailMap = new TreeMap<>();
	private final TreeMap<Integer, BukkitTask> tasks = new TreeMap<>();

	public TrailsHandler(JavaPlugin plugin) {
		instance = this;
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void addTrail(Entity entity, int ticks, EffectPlayer effectPlayer) {
		entityTickMap.put(entity, ticks);

		Collection<Trail> trails = trailMap.getOrDefault(ticks, new HashSet<>());
		trails.add(new Trail(entity, effectPlayer, ticks));

		// Start scheduler if first entry
		if (trails.size() == 1) {
			BukkitTask scheduler = tasks.get(ticks);
			if (scheduler == null || scheduler.isCancelled()) {
				scheduler = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
					trailMap.getOrDefault(ticks, new HashSet<>()).forEach(trail -> trail.effectPlayer.play(trail.entity.getLocation()));
				}, 0, ticks);
				tasks.put(ticks, scheduler);
			}
		}
		trailMap.put(ticks, trails);
	}

	public void removeTrail(Entity entity) {
		if (!entityTickMap.containsKey(entity)) {
			return;
		}
		int ticks = entityTickMap.get(entity);
		entityTickMap.remove(entity);

		Collection<Trail> trails = trailMap.get(ticks);
		if (trails != null) {
			trails.remove(trails.stream().filter(trail -> trail.entity.equals(entity)).findFirst().orElse(null));

			if (trails.size() == 0) {
				tasks.get(ticks).cancel();
				tasks.remove(ticks);
			}
		}
	}

	@EventHandler
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		removeTrail(event.getEntity());
	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		removeTrail(event.getEntity());
	}
}
