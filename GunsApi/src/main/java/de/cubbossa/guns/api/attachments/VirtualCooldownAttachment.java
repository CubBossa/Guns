package de.cubbossa.guns.api.attachments;

import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunAction;
import de.cubbossa.guns.api.context.GunActionContext;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.Consumer;

public class VirtualCooldownAttachment extends LogicAttachment {

	private static HashMap<Gun, Long> COOLDOWNS = new HashMap<>();

	private int millseconds;
	private Consumer<Player> cooldownInformation;

	public VirtualCooldownAttachment(NamespacedKey key, int ms, Consumer<Player> cooldownInformation) {
		super(key);
		this.millseconds = ms;
		this.cooldownInformation = cooldownInformation;
	}

	public void install(Gun gun, ItemStack stack) {

	}

	public void uninstall(Gun gun, ItemStack stack) {

	}

	public <C extends GunActionContext> void perform(GunAction<C> action, C context) {
		Long insertion = COOLDOWNS.get(context.getGun());
		long current = System.currentTimeMillis();
		if (insertion != null && current - insertion < millseconds) {
			context.setCancelled(true);
			cooldownInformation.accept(context.getPlayer());
			return;
		}
		COOLDOWNS.put(context.getGun(), current);
	}
}
