package de.cubbossa.guns.api.context;

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

	private final Player player;
	private final ItemStack stack;
	private final Cancellable cancellable;
	private boolean cancelled = false;
}
