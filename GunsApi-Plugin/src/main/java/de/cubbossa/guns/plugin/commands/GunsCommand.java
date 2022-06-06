package de.cubbossa.guns.plugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.attachments.VanillaCooldownAttachment;
import de.cubbossa.guns.api.impact.EntityImpact;
import de.cubbossa.guns.plugin.*;
import de.cubbossa.guns.plugin.handler.ObjectsHandler;
import de.cubbossa.menuframework.inventory.Action;
import de.cubbossa.menuframework.inventory.Button;
import de.cubbossa.menuframework.inventory.implementations.ListMenu;
import nbo.NBOFile;
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

		}).thenRun(() -> {
			GunsAPI.sendMessage(sender, Messages.RELOAD_SUCCESS, TagResolver.resolver("ms", Tag.preProcessParsed(System.currentTimeMillis() - now + "")));

		}).exceptionally(throwable -> {
			GunsAPI.sendMessage(sender, Messages.RELOAD_ERROR);
			GunsAPI.log(Level.SEVERE, "Could not reload file, errors occured: ", throwable);
			return null;
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
		Attachment attachment = new VanillaCooldownAttachment(new NamespacedKey(GunsAPI.getInstance(), "cooldown"), 10);
		GunsHandler.getInstance().getAttachmentRegistry().put(attachment);

		ListMenu menu = new ListMenu(Messages.GUNS_GUI_TITLE, 4);
		for (Gun gun : GunsHandler.getInstance().getGunsRegistry()) {
			menu.addListEntry(Button.builder()
					.withItemStack(gun.createWeaponStack())
					.withClickHandler(Action.LEFT, clickContext -> {
						ItemStack stack = gun.createWeaponStack();
						GunsHandler.getInstance().setAmmunition(stack, GunsHandler.getInstance().getAmmoRegistry().get(NamespacedKey.fromString("gunsapi:simple_ammo")), 8);
						GunsHandler.getInstance().updateItemStack(stack, gun, GunsHandler.getInstance().getAmmoRegistry().get(NamespacedKey.fromString("gunsapi:simple_ammo")), 8);
						GunsHandler.getInstance().addAttachment(stack, attachment);
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
