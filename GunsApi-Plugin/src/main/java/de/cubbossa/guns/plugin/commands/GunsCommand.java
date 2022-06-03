package de.cubbossa.guns.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.plugin.*;
import de.cubbossa.guns.plugin.handler.ObjectsHandler;
import de.cubbossa.menuframework.inventory.Action;
import de.cubbossa.menuframework.inventory.Button;
import de.cubbossa.menuframework.inventory.implementations.ListMenu;
import nbo.NBOFile;
import nbo.NBOParseException;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@CommandAlias("guns")
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

	@Subcommand("createFile")
	public void onCreateFile(CommandSender sender) throws NBOParseException, ClassNotFoundException, IOException {
		NBOFile file = NBOFile.loadString("", ObjectsHandler.getInstance().getSerializer());

		SerializableProjectile projectile = new SerializableProjectile();
		projectile.setProjectileType(EntityType.ARROW);

		SerializableAmmunition ammo = new SerializableAmmunition(new NamespacedKey(GunsAPI.getInstance(), "simple_ammo"));
		ammo.setMagazineSize(8);
		ammo.setNameFormat("<gray>Just ammo");
		ammo.setProjectile(projectile);

		SerializableGun gun = new SerializableGun(new NamespacedKey(GunsAPI.getInstance(), "simple_gun"));
		gun.addValidAmmunition(ammo);

		file.setObject("just_projectile", projectile);
		file.setObject("just_ammo", ammo);
		file.setObject("just_gun", gun);

		file.save(new File(GunsAPI.getInstance().getDataFolder(), "guns.nbo"));
	}

	@Subcommand("guns")
	public void onGuns(Player player) {
		ListMenu menu = new ListMenu(Messages.GUNS_GUI_TITLE, 4);
		for (Gun gun : ObjectsHandler.getInstance().getGunsRegistry().values()) {
			menu.addListEntry(Button.builder()
					.withItemStack(gun.createWeaponStack())
					.withClickHandler(Action.LEFT, clickContext -> {
						ItemStack stack = gun.createWeaponStack();
						GunsHandler.getInstance().setAmmunition(stack, ObjectsHandler.getInstance().getAmmunitionRegistry().get("just_ammo"), 8);
						GunsHandler.getInstance().updateItemStack(stack, gun, ObjectsHandler.getInstance().getAmmunitionRegistry().get("just_ammo"), 8);
						clickContext.getPlayer().getInventory().addItem(gun.createWeaponStack());
					}));
		}
		menu.open(player);
	}

	@Subcommand("ammo|ammunition")
	public void onAmmo(Player player) {
		ItemStack gunStack = player.getInventory().getItemInMainHand();
		if (!GunsHandler.getInstance().isGun(gunStack)) {
			GunsAPI.sendMessage(player, Messages.GUI_MUST_HOLD_GUN);
		}
	}

	@Subcommand("attach|attachment")
	public void onAttach(Player player) {
		ItemStack gunStack = player.getInventory().getItemInMainHand();
		if (!GunsHandler.getInstance().isGun(gunStack)) {
			GunsAPI.sendMessage(player, Messages.GUI_MUST_HOLD_GUN);
		}
	}

	@Subcommand("editor effects")
	public void onEditorEffects(Player player) {
		//preview item

	}
}
