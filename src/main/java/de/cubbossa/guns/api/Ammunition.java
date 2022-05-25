package de.cubbossa.guns.api;

import org.bukkit.entity.Player;

public interface Ammunition {

    int getCount(Player player);

    void recharge(Gun gun);

    boolean removeCount(Player player, int amount);
}
