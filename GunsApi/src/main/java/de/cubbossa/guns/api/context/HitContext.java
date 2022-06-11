package de.cubbossa.guns.api.context;

import de.cubbossa.guns.api.Gun;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class HitContext extends GunActionContext {

	public HitContext(Gun gun, Player player, ItemStack stack, Cancellable cancellable) {
		super(gun, player, stack, cancellable);
	}
}
