package de.cubbossa.guns.plugin;

import com.google.common.collect.Lists;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunListener;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.ParticleLinePlayer;
import de.cubbossa.guns.api.effects.ParticlePlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import de.cubbossa.guns.implementations.SimpleGun;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class GunsAPI extends JavaPlugin {

	/*
	Schrotflinte
	Granatwerfer
	Nebelgranaten
	Flammenwerfer
	Laserpointer
	Rotpunktvisier
	Infrarotvisier
	Nachtsichtvisier
	Doppeltes Magazin
	Schalldämpfer
	Bayonett
	Griff -> akkurat
	 */

	public void onEnable() {

		new GunsHandler(this);
		Bukkit.getPluginManager().registerEvents(new GunListener(), this);

		Gun gun = new SimpleGun(new NamespacedKey(this, "test-gun"));
		gun.setNoAmmunitionEffectFactory(() -> new SoundPlayer(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f));
		gun.setName("<rainbow>Rainbow Gun</rainbow>");
		gun.setLore(Lists.newArrayList("<gray>Line1</gray>", "<gray>Line2</gray>"));
		gun.setMuzzleFlashFactory(() -> new EffectPlayer()
				.withEffect(0, new SoundPlayer(Sound.ENTITY_WITHER_BREAK_BLOCK, .15f, .5f))
				.withEffect(0, new SoundPlayer(Sound.ENTITY_FIREWORK_ROCKET_BLAST, .4f, .25f))
				.withEffect(0, new SoundPlayer(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 4f, 2f))
				.withEffect(0, new SoundPlayer(Sound.BLOCK_IRON_DOOR_OPEN, 2f, 1.5f))
				.withEffect(0, new ParticlePlayer(Particle.FLAME, 0, new Vector(.01, .01, .01)))
				.withEffect(0, new ParticleLinePlayer(Particle.SMOKE_NORMAL, 0, new Vector(.01, .01, .01), 1, .1f)
						.withMotion(new Vector(.2, .2, .2)))
				.withEffect(4, new SoundPlayer(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 2f)));

		getCommand("gunsapi").setExecutor((sender, cmd, label, args) -> {
			((Player) sender).getInventory().addItem(gun.createWeaponStack());
			return false;
		});
	}
}
