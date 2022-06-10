package de.cubbossa.guns.api.context;

import de.cubbossa.guns.api.Gun;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
@RequiredArgsConstructor
public class GunActionContext {

	private final Gun gun;
	private final Player player;
	private final ItemStack stack;
	private final Cancellable cancellable;
	private boolean cancelled = false;
}
