package de.cubbossa.guns.api;

import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Keyed;
import org.bukkit.entity.Player;

public interface Ammunition extends Keyed {

    ComponentLike getName();

    int getCount(Player player);

    void addCount(Player player, int amount);

    void removeCount(Player player, int amount);

    int getBulletCount();

    GunProjectile getProjectile();
}
