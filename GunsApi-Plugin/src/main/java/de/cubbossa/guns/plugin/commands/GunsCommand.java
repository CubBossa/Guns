package de.cubbossa.guns.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.impact.EntityImpact;
import de.cubbossa.guns.plugin.*;
import de.cubbossa.guns.plugin.editor.GunsBuilder;
import de.cubbossa.guns.plugin.handler.ObjectsHandler;
import nbo.NBOFile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

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

		}).exceptionally(throwable -> {
			GunsAPI.sendMessage(sender, Messages.RELOAD_ERROR, TagResolver.builder()
					.tag("error", Tag.inserting(Component.text(throwable.getMessage())))
					.build());
			GunsAPI.log(Level.SEVERE, "Could not reload guns.nbo, errors occured: ", throwable);
			return null;

		}).thenRun(() -> {
			GunsAPI.sendMessage(sender, Messages.RELOAD_SUCCESS, TagResolver.builder()
					.tag("ms", Tag.preProcessParsed(System.currentTimeMillis() - now + ""))
					.build());
		});
	}

	@Subcommand("createFile")
	public void onCreateFile(CommandSender sender) throws IOException {
		NBOFile file = new NBOFile();
		file.setSerializer(ObjectsHandler.getInstance().getSerializer());

		SerializableProjectile projectile = new SerializableProjectile();

		SerializableAmmunition ammo = new SerializableAmmunition(new NamespacedKey(GunsAPI.getInstance(), "simple_ammo"));
		ammo.setBulletCount(8);
		ammo.setNameFormat("<gray>Just ammo");
		ammo.setProjectile(projectile);

		SerializableGun gun = new SerializableGun(new NamespacedKey(GunsAPI.getInstance(), "simple_gun"));
		gun.addValidAmmunition(ammo);

		EntityImpact impact = new EntityImpact();
		impact.setVelocity(new Vector(1, 2, 1023.9128));
		impact.setDamage(123);
		impact.setEffectPlayer(GunsAPI.EFFECT);
		impact.getEffects().add(new PotionEffect(PotionEffectType.BLINDNESS, 1, 2, true));

		file.setObject("just_impact", impact);
		file.setObject("just_projectile", projectile);
		file.setObject("just_ammo", ammo);
		file.setObject("just_gun", gun);

		File f = new File(GunsAPI.getInstance().getDataFolder(), "guns_gen.nbo");
		if (!f.exists()) {
			f.createNewFile();
		}
		file.save(f);
	}

	@Subcommand("guns")
	public void onGuns(Player player) {
		GunsBuilder.createGunsMenu(player).open(player);
	}

	@Subcommand("ammo|ammunition")
	public void onAmmo(Player player) {
		ItemStack gunStack = player.getInventory().getItemInMainHand();
		if (!GunsHandler.getInstance().isGun(gunStack)) {
			GunsAPI.sendMessage(player, Messages.GUI_MUST_HOLD_GUN);
		}
		GunsBuilder.createAmmoMenu(gunStack, player).open(player);
	}

	@Subcommand("attach|attachment")
	public void onAttach(Player player) {
		ItemStack gunStack = player.getInventory().getItemInMainHand();
		if (!GunsHandler.getInstance().isGun(gunStack)) {
			GunsAPI.sendMessage(player, Messages.GUI_MUST_HOLD_GUN);
		}
		GunsBuilder.createAttachmentMenu(gunStack, player).open(player);
	}

	@Subcommand("shed effects")
	public void onEditorEffects(Player player) {
		//preview item

	}
}
