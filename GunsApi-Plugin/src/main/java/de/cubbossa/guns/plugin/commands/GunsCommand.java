package de.cubbossa.guns.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.plugin.GunsAPI;
import de.cubbossa.guns.plugin.Messages;
import de.cubbossa.guns.plugin.handler.ObjectsHandler;
import de.cubbossa.menuframework.inventory.Action;
import de.cubbossa.menuframework.inventory.Button;
import de.cubbossa.menuframework.inventory.implementations.ListMenu;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class GunsCommand extends BaseCommand {

	@Default
	public void onDefault(CommandSender sender) {

	}

	@Subcommand("reload")
	public void onReload(CommandSender sender) {
		long now = System.currentTimeMillis();

		CompletableFuture.runAsync(() -> {
			ObjectsHandler.getInstance().loadFile();

		}).thenRun(() -> {
			GunsAPI.sendMessage(sender, Messages.RELOAD_SUCCESS, TagResolver.resolver("ms", Tag.preProcessParsed(System.currentTimeMillis() - now + "")));

		}).exceptionally(throwable -> {
			GunsAPI.sendMessage(sender, Messages.RELOAD_ERROR);
			GunsAPI.log(Level.SEVERE, "Could not reload file, errors occured: ", throwable);
			return null;
		});
	}

	@Subcommand("guns")
	public void onGuns(Player player) {
		ListMenu menu = new ListMenu(Messages.GUNS_GUI_TITLE, 4);
		for (Gun gun : ObjectsHandler.getInstance().getGunsRegistry().values()) {
			menu.addListEntry(Button.builder()
					.withItemStack(gun.createWeaponStack())
					.withClickHandler(Action.LEFT, clickContext -> {

					}));
		}
		menu.open(player);
	}
}
