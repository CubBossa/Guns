package de.cubbossa.guns.api;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

public interface Ammunition extends Keyed {

    ComponentLike getName();

    int getCount(Player player);

    int getMagazineCount();

    GunProjectile getProjectile();
}
