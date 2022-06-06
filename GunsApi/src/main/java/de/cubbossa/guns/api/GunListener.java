package de.cubbossa.guns.api;

import de.cubbossa.guns.api.context.RechargeContext;
import de.cubbossa.guns.api.context.ShootContext;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class GunListener implements Listener {

	@Getter
	private static GunListener instance;

	public final HashMap<UUID, Impact<Entity>> entityImpacts = new HashMap<>();
	public final HashMap<UUID, Impact<Block>> blockImpacts = new HashMap<>();

	public GunListener() {
		instance = this;
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		try {
			//TODO actually senseless to create ful contexts here.
			if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				GunsHandler.getInstance().perform(GunsHandler.ACTION_RECHARGE, new RechargeContext(event.getPlayer(), event.getItem(), event, null));
			} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				GunsHandler.getInstance().perform(GunsHandler.ACTION_SHOOT, new ShootContext(event.getPlayer(), event.getItem(), event));
			}
		} catch (Throwable e) {
			GunsHandler.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Could not call action for gun.", e);
		}
	}


	@EventHandler
	public void onProjectileHitEntity(EntityDamageByEntityEvent event) {
		Impact<Entity> impact = entityImpacts.get(event.getDamager().getUniqueId());
		if (impact == null || !(event.getDamager() instanceof Projectile projectile)) {
			return;
		}
		event.setDamage(impact.handleHit(projectile, event.getEntity(), event.getDamager().getLocation()));
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {

	}

	@EventHandler
	public void onProjectileDeath(EntityDeathEvent event) {

		UUID uuid = event.getEntity().getUniqueId();
		entityImpacts.remove(uuid);
		blockImpacts.remove(uuid);
	}
}
