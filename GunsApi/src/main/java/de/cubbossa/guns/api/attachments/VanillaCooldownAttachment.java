package de.cubbossa.guns.api.attachments;

import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunAction;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.context.GunActionContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class VanillaCooldownAttachment extends LogicAttachment {

	private int ticks;

	public VanillaCooldownAttachment(NamespacedKey key, int ticks) {
		super(key);
		this.ticks = ticks;
	}

	public void install(Gun gun, ItemStack stack) {

	}

	public void uninstall(Gun gun, ItemStack stack) {

	}

	public <C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable {
		if (context.getPlayer().hasCooldown(context.getStack().getType())) {
			context.setCancelled(true);
			return;
		}
		context.getPlayer().setCooldown(context.getStack().getType(), ticks);
	}
}
