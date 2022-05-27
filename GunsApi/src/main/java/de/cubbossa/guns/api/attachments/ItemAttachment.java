package de.cubbossa.guns.api.attachments;

import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunAction;
import de.cubbossa.guns.api.context.GunActionContext;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class ItemAttachment implements Attachment {

	@Override public NamespacedKey getKey() {
		return null;
	}

	public void install(Gun gun, ItemStack stack) {

	}

	public void uninstall(Gun gun, ItemStack stack) {

	}

	public <C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable {

	}
}
