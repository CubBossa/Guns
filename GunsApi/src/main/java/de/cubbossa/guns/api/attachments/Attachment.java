package de.cubbossa.guns.api.attachments;

import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunAction;
import de.cubbossa.guns.api.context.GunActionContext;
import org.bukkit.inventory.ItemStack;

public interface Attachment {

	void install(Gun gun, ItemStack stack);

	void uninstall(Gun gun, ItemStack stack);

	<C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable;
}
