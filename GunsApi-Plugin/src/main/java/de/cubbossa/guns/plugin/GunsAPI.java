package de.cubbossa.guns.plugin;

import co.aikar.commands.BukkitCommandManager;
import de.cubbossa.guns.api.GunListener;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.TrailsHandler;
import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.ParticleLinePlayer;
import de.cubbossa.guns.api.effects.ParticlePlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import de.cubbossa.guns.plugin.commands.GunsCommand;
import de.cubbossa.guns.plugin.handler.ObjectsHandler;
import de.cubbossa.menuframework.GUIHandler;
import de.cubbossa.translations.Message;
import de.cubbossa.translations.TranslationHandler;
import lombok.Getter;
import lombok.SneakyThrows;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Locale;
import java.util.logging.Level;

@Getter
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

	public static final EffectPlayer EFFECT = new EffectPlayer()
			.withEffect(new SoundPlayer(Sound.ENTITY_WITHER_BREAK_BLOCK, .15f, .5f))
			.withEffect(new SoundPlayer(Sound.ENTITY_FIREWORK_ROCKET_BLAST, .4f, .25f))
			.withEffect(new SoundPlayer(Sound.ENTITY_FIREWORK_ROCKET_BLAST, 4f, 2f))
			.withEffect(new SoundPlayer(Sound.BLOCK_IRON_DOOR_OPEN, 2f, 1.5f))
			.withEffect(new ParticlePlayer(Particle.FLAME, 0, new Vector(.01, .01, .01)))
			.withEffect(new ParticleLinePlayer(Particle.DUST_COLOR_TRANSITION, 0, new Vector(.01, .01, .01), 1, .1f)
					.withMotion(new Vector(.2, .2, .2))
					.withData(new Particle.DustTransition(Color.fromRGB(0xFFCC66), Color.fromBGR(0xB3B3B3), 1f)))
			.withEffect(4, new SoundPlayer(Sound.BLOCK_IRON_TRAPDOOR_CLOSE, 1f, 2f));


	@Getter
	private static GunsAPI instance;
	private MiniMessage miniMessage;
	private BukkitAudiences audiences;

	@SneakyThrows public void onEnable() {

		instance = this;

		saveResource("imports.nbo", false);
		saveResource("effects.nbo", false);
		saveResource("impacts.nbo", false);
		saveResource("projectiles.nbo", false);
		saveResource("ammo.nbo", false);
		saveResource("guns.nbo", false);

		new GunsHandler(this);
		ObjectsHandler objectsHandler = new ObjectsHandler();
		objectsHandler.registerDefaults();
		objectsHandler.loadFile();

		audiences = BukkitAudiences.create(this);
		miniMessage = MiniMessage.miniMessage();

		TranslationHandler translationHandler = new TranslationHandler(this, audiences, miniMessage, new File(getDataFolder(), "/lang/"), "lang");
		translationHandler.setFallbackLanguage("en_US");
		translationHandler.setUseClientLanguage(true);

		new GUIHandler(this).enable();

		BukkitCommandManager commandManager = new BukkitCommandManager(this);
		commandManager.addSupportedLanguage(Locale.ENGLISH);
		commandManager.registerCommand(new GunsCommand());

		Bukkit.getPluginManager().registerEvents(new GunListener(), this);

		new TrailsHandler(this);
	}

	@Override public void onDisable() {
		GUIHandler.getInstance().disable();
	}

	public static void log(Level level, String message, Throwable t) {
		instance.getLogger().log(level, message, t);
	}

	public static void sendMessage(CommandSender sender, Message message, TagResolver... resolvers) {
		var aud = instance.audiences.sender(sender);
		aud.sendMessage(TranslationHandler.getInstance().translateLine(message, aud, resolvers));
	}
}
