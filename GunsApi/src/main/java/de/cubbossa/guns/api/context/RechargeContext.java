package de.cubbossa.guns.api.context;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.Gun;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

@Getter
@Setter
public class RechargeContext extends GunActionContext {

    private Ammunition ammunition;

    public RechargeContext(Gun gun, Player player, ItemStack stack, Cancellable cancellable, Ammunition ammunition) {
        super(gun, player, stack, cancellable);
        this.ammunition = ammunition;
    }
}
