package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.EffectPlayer;
import de.cubbossa.guns.api.GunListener;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.implementations.SimpleGun;
import de.cubbossa.guns.implementations.SimpleProjectile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class GunsAPI extends JavaPlugin {

	public void onEnable() {

		new GunsHandler(this);
		Bukkit.getPluginManager().registerEvents(new GunListener(), this);

		SimpleGun gun = new SimpleGun(new NamespacedKey(this, "test-gun"));
		gun.setItemStack(new ItemStack(Material.LEVER));
		gun.setProjectileFactory(SimpleProjectile::new);
		gun.setMuzzleFlashFactory(EffectPlayer::new);

		getCommand("gunsapi").setExecutor((sender, cmd, label, args) -> {
			((Player) sender).getInventory().addItem(gun.createWeaponStack());
			return false;
		});
	}
}
