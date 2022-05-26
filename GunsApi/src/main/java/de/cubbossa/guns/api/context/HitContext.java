package de.cubbossa.guns.api.context;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class HitContext extends GunActionContext {

	public HitContext(Player player, ItemStack stack, Cancellable cancellable) {
		super(player, stack, cancellable);
	}
}
