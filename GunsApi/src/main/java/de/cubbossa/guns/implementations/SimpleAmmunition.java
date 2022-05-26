package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunProjectile;
import de.cubbossa.guns.api.GunsHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

@Getter
@Setter
public class SimpleAmmunition implements Ammunition {

	private final NamespacedKey key;
	private ComponentLike name = Component.empty();
	private int magazineCount = 32;

	public SimpleAmmunition(NamespacedKey key) {
		this.key = key;
		GunsHandler.getInstance().registerAmmunition(this);
	}

	public int getCount(Player player) {
		return Integer.MAX_VALUE;
	}

	public void recharge(Gun gun, Player player) {

	}

	public boolean removeCount(Player player, int amount) {
		return true;
	}

	public GunProjectile getProjectile() {
		return new SimpleProjectile();
	}
}
