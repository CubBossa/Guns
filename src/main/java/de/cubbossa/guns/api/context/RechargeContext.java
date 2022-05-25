package de.cubbossa.guns.api.context;

import de.cubbossa.guns.api.Ammunition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Setter
public class RechargeContext {

    private final Player player;
    private Ammunition ammunition;
    private boolean cancelled = false;

    public RechargeContext(Player player, Ammunition ammunition) {
        this.player = player;
        this.ammunition = ammunition;
    }
}
