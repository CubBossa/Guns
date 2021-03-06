package de.cubbossa.guns.plugin.editor;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.plugin.GunsAPI;
import de.cubbossa.guns.plugin.Messages;
import de.cubbossa.menuframework.inventory.*;
import de.cubbossa.menuframework.inventory.implementations.AnvilMenu;
import de.cubbossa.menuframework.inventory.implementations.ListMenu;
import de.cubbossa.translations.MenuIcon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class GunsBuilder {

	public static TopMenu createGunsMenu(@Nullable Player languageOwner) {
		AtomicReference<Gun> selection = new AtomicReference<>();
		AtomicReference<ItemStack> gunItem = new AtomicReference<>(new ItemStack(Material.AIR));
		List<Attachment> activeAttachments = new ArrayList<>();

		ListMenu menu = new ListMenu(Messages.G_GUNS_TITLE, 4);
		menu.addPreset(GunsEditor.bottomRow(3));
		menu.addPreset(presetApplier -> {
			presetApplier.addItemOnTop(3 * 9 + 4, ItemStackUtils.createInfoItem(Messages.G_GUNS_INFO_NAME, Messages.G_GUNS_INFO_LORE));
		});
		for (Gun gun : GunsHandler.getInstance().getGunsRegistry()) {
			if (languageOwner != null && !gun.getUsePredicate().test(languageOwner)) {
				continue;
			}
			menu.addListEntry(Button.builder()
					.withItemStack(() -> Objects.equals(selection.get(), gun) ? ItemStackUtils.setGlow(gun.createWeaponStack()) : gun.createWeaponStack())
					.withClickHandler(Action.RIGHT, context -> {
						if (!gun.getUsePredicate().test(context.getPlayer())) {
							GunsAPI.SOUND_DECLINE.accept(context.getPlayer());
							return;
						}
						selection.set(gun);
						gunItem.set(gun.createWeaponStack());
						menu.refreshDynamicItemSuppliers();
						menu.refresh(menu.getSlots());
					})
					.withClickHandler(Action.LEFT, context -> {
						if (!gun.getUsePredicate().test(context.getPlayer())) {
							GunsAPI.SOUND_DECLINE.accept(context.getPlayer());
							return;
						}
						context.getPlayer().getInventory().addItem(gun.createWeaponStack());
						context.getMenu().close(context.getPlayer());
					}));
		}
		menu.addPreset(presetApplier -> {
			if (selection.get() != null) {
				presetApplier.addItemOnTop(3 * 9 + 6, new MenuIcon(ItemStackUtils.MATERIAL_ATTACH, Messages.G_GUNS_ATTACH_NAME, Messages.G_GUNS_ATTACH_LORE).createItem());
				presetApplier.addClickHandlerOnTop(3 * 9 + 6, Action.LEFT, clickContext -> {
					if (clickContext.getMenu() instanceof TopInventoryMenu topMenu) {
						topMenu.openSubMenu(clickContext.getPlayer(), createAttachmentMenu(selection.get(), gunItem.get(), activeAttachments));
					}
				});
				presetApplier.addItemOnTop(3 * 9 + 7, new MenuIcon(ItemStackUtils.MATERIAL_AMMO, Messages.G_GUNS_AMMO_NAME, Messages.G_GUNS_AMMO_LORE).createItem());
				presetApplier.addClickHandlerOnTop(3 * 9 + 7, Action.LEFT, clickContext -> {
					if (clickContext.getMenu() instanceof TopInventoryMenu topMenu) {
						topMenu.openSubMenu(clickContext.getPlayer(), createAmmoMenu(gunItem.get()));
					}
				});
			} else {
				presetApplier.addItemOnTop(3 * 9 + 6, () -> null);
				presetApplier.addClickHandlerOnTop(3 * 9 + 6, Action.LEFT, context -> {
				});
				presetApplier.addItemOnTop(3 * 9 + 7, () -> null);
				presetApplier.addClickHandlerOnTop(3 * 9 + 7, Action.LEFT, context -> {
				});
			}
		});
		return menu;
	}

	public static TopMenu createAttachmentMenu(ItemStack gunStack) {
		Gun gun = GunsHandler.getInstance().getGun(gunStack);
		List<Attachment> attachments = GunsHandler.getInstance().getAttachments(gunStack);

		return createAttachmentMenu(gun, gunStack, attachments);
	}

	public static TopMenu createAttachmentMenu(Gun gun, ItemStack gunStack, List<Attachment> activeAttachments) {
		ListEditorMenu<Attachment> menu = new ListEditorMenu<>(Messages.G_ATTACH_TITLE, 4, new ListMenuSupplier<>() {
			@Override
			public Collection<Attachment> getElements() {
				return new ArrayList<>(gun.getAttachments());
			}

			@Override
			public ItemStack getDisplayItem(Attachment attachment) {
				return null; //TODO
			}
		});
		menu.setInfoItem(Messages.G_ATTACH_INFO_NAME, Messages.G_ATTACH_INFO_LORE);
		menu.setItemModifier((attachment, stack) -> activeAttachments.contains(attachment) ? ItemStackUtils.setGlow(stack) : stack);
		menu.addPreset(MenuPresets.back(3 * 9 + 8, Action.LEFT));
		menu.addPreset(presetApplier -> {
			presetApplier.addItemOnTop(3 * 9 + 7, Icon.ACCEPT_RP);
			presetApplier.addClickHandlerOnTop(3 * 9 + 7, Action.LEFT, context -> {
				for (Attachment attachment : activeAttachments) {
					GunsHandler.getInstance().addAttachment(gunStack, attachment);
				}
				if (context.getMenu() instanceof TopMenu topMenu && topMenu.getPrevious(context.getPlayer()) != null) {
					topMenu.openPreviousMenu(context.getPlayer());
				}
			});
		});
		return menu;
	}

	public static TopMenu createAmmoMenu(ItemStack gunStack) {
		Gun gun = GunsHandler.getInstance().getGun(gunStack);
		if (gun == null) {
			throw new IllegalArgumentException("Provided ItemStack cannot be resolved to a valid gun type.");
		}

		AtomicReference<Ammunition> activeAmmunition = new AtomicReference<>();
		AtomicInteger ammunitionCount = new AtomicInteger(1);

		ListEditorMenu<Ammunition> menu = new ListEditorMenu<>(Messages.G_AMMO_TITLE, 4, new ListMenuSupplier<>() {
			@Override
			public Collection<Ammunition> getElements() {
				return new ArrayList<>(gun.getValidAmmunition());
			}

			@Override
			public ItemStack getDisplayItem(Ammunition ammunition) {
				return null; //TODO
			}
		});
		menu.setInfoItem(Messages.G_AMMO_INFO_NAME, Messages.G_AMMO_INFO_LORE);
		menu.setItemModifier((ammo, stack) -> Objects.equals(activeAmmunition.get(), ammo) ? ItemStackUtils.setGlow(stack) : stack);
		menu.setClickHandler(Action.LEFT, context -> {
			activeAmmunition.set(context.getTarget());
			ammunitionCount.set(context.getTarget().getBulletCount());
		});

		menu.addPreset(MenuPresets.back(3 * 9 + 8, Action.LEFT));
		menu.addPreset(presetApplier -> {
			presetApplier.addItemOnTop(3 * 9 + 5, new MenuIcon.Builder(new ItemStack(Material.PAPER, ammunitionCount.get()))
					.withName(Messages.G_AMMO_COUNT_NAME)
					.withLore(Messages.G_AMMO_COUNT_LORE)
					.withLoreResolver(TagResolver.builder()
							.tag("amount", Tag.inserting(Component.text(ammunitionCount.get()))).build())
					.build()
					.createItem());
			presetApplier.addClickHandlerOnTop(3 * 9 + 5, Action.LEFT, clickContext -> {

				AnvilMenu m = GunsEditor.newAnvilMenu(Messages.G_AMMO_COUNT_TITLE, ammunitionCount.get() + "", AnvilInputValidator.VALIDATE_INT);
				m.setOutputClickHandler(AnvilMenu.CONFIRM, s -> {
					ammunitionCount.set(Integer.parseInt(s.getTarget()));
					if (s.getMenu() instanceof TopMenu topMenu) {
						topMenu.openPreviousMenu(s.getPlayer());
					}
				});
				menu.openSubMenu(clickContext.getPlayer(), m);
			});
		});
		menu.addPreset(presetApplier -> {
			presetApplier.addItemOnTop(3 * 9 + 7, Icon.ACCEPT_RP);
			presetApplier.addClickHandlerOnTop(3 * 9 + 7, Action.LEFT, context -> {
				GunsHandler.getInstance().setAmmunition(gunStack, activeAmmunition.get(), ammunitionCount.get());
				if (context.getMenu() instanceof TopMenu topMenu && topMenu.getPrevious(context.getPlayer()) != null) {
					topMenu.openPreviousMenu(context.getPlayer());
				}
			});
		});
		return menu;
	}
}
