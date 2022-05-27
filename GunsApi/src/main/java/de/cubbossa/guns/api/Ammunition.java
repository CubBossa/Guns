package de.cubbossa.guns.api;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface Ammunition {

    NamespacedKey getKey();

    ComponentLike getName();

    int getCount(Player player);

    int getMagazineCount();

    GunProjectile getProjectile();
}
