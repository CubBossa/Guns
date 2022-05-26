package de.cubbossa.guns.api;

import de.cubbossa.guns.api.context.RechargeContext;
import de.cubbossa.guns.api.context.ShootContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Level;

public class GunListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		try {
			//TODO actually senseless to create ful contexts here.
			if(event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
				GunsHandler.getInstance().perform(GunsHandler.ACTION_RECHARGE, new RechargeContext(event.getPlayer(), event.getItem(), event, null));
			} else if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				GunsHandler.getInstance().perform(GunsHandler.ACTION_SHOOT, new ShootContext(event.getPlayer(), event.getItem(), event));
			}
		} catch (Throwable e) {
			GunsHandler.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Could not call action for gun.", e);
		}
	}
}
