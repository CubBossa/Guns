package de.cubbossa.guns.api;

import de.cubbossa.guns.api.context.HitContext;
import de.cubbossa.guns.api.context.RechargeContext;
import de.cubbossa.guns.api.context.ShootContext;
import org.bukkit.inventory.ItemStack;

public interface Attachment {

	void install(Gun gun, ItemStack stack);

	void uninstall(Gun gun, ItemStack stack);

	void hit(HitContext context);

	void recharge(RechargeContext context);

	void shoot(ShootContext context);
}
