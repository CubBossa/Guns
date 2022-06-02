package de.cubbossa.guns.plugin;

import com.google.common.collect.Lists;
import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.GunListener;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.ParticleLinePlayer;
import de.cubbossa.guns.api.effects.ParticlePlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import de.cubbossa.guns.plugin.handler.ObjectsHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import nbo.NBOFile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

	public static final EffectPlayer effect = new EffectPlayer()
			.withEffect(new SoundPlayer(Sound.ENTITY_WITHER_BREAK_BLOCK, .15f, .5f))
			.withEffect(new SoundPlayer(Sound.ENTITY_FIREWORK_ROCKET_BLAST, .4f, .25f))
			.withEffect(new SoundPlayer(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 4f, 2f))
			.withEffect(new SoundPlayer(Sound.BLOCK_IRON_DOOR_OPEN, 2f, 1.5f))
			.withEffect(new ParticlePlayer(Particle.FLAME, 0, new Vector(.01, .01, .01)))
			.withEffect(new ParticleLinePlayer(Particle.DUST_COLOR_TRANSITION, 0, new Vector(.01, .01, .01), 1, .1f)
					.withMotion(new Vector(.2, .2, .2)))
					//.withData(new Particle.DustTransition(Color.fromRGB(0xFFCC66), Color.fromBGR(0xB3B3B3), 1f)))
			.withEffect(4, new SoundPlayer(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 2f));


	@Getter
	private static GunsAPI instance;

	@SneakyThrows public void onEnable() {

		instance = this;

		new GunsHandler(this);
		new ObjectsHandler().registerDefaults();

		Bukkit.getPluginManager().registerEvents(new GunListener(), this);

		SerializableGun gun = new SerializableGun(new NamespacedKey(this, "test-gun"));
		Ammunition ammo = new SimpleAmmunition(new NamespacedKey(this, "test-ammo"));

		//gun.addValidAmmunition(ammo);

		gun.setNoAmmunitionEffectFactory(() -> new EffectPlayer()
				.withEffect(new SoundPlayer(Sound.BLOCK_STONE_BUTTON_CLICK_ON, .6f, 2f))
				.withEffect(new SoundPlayer(Sound.ITEM_SPYGLASS_USE, 1f, 1f)));
		gun.setName("<rainbow>Rainbow Gun</rainbow>");
		gun.setLore(Lists.newArrayList("<gray>Line1</gray>", "<gray>Line2</gray>"));

		gun.setMuzzleEffect(effect);

		gun.setNoAmmoEffect(new EffectPlayer()
				.withEffect(new SoundPlayer(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1.5f))
				.withEffect(4, new SoundPlayer(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1.f))
				.withEffect(8, new SoundPlayer(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 1.2f)));

		getCommand("gunsapi").setExecutor((sender, cmd, label, args) -> {
			ItemStack stack = gun.createWeaponStack();
			gun.setAmmunitionCharged(stack, ammo, 32);
			GunsHandler.getInstance().updateItemStack(stack, gun, ammo, 32);
			//GunsHandler.getInstance().addAttachment(stack, attachment);
			((Player) sender).getInventory().addItem(stack);
			return false;
		});

		var file = NBOFile.loadString("", ObjectsHandler.getInstance().getSerializer());
		file.setObject("effect", effect);
		file.setObject("a gun", gun);
		System.out.println(NBOFile.formatToFileString(file.getRoot()));
	}
}
