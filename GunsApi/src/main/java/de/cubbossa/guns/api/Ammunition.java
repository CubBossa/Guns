package de.cubbossa.guns.api;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface Ammunition {

    NamespacedKey getKey();

    int getCount(Player player);

    int getMagazineCount();

    void recharge(Gun gun);

    boolean removeCount(Player player, int amount);

    GunProjectile getProjectile();
}
