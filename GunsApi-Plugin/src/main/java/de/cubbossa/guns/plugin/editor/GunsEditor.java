package de.cubbossa.guns.plugin.editor;

import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.impact.BlockImpact;
import de.cubbossa.guns.api.impact.EntityImpact;
import de.cubbossa.guns.plugin.Messages;
import de.cubbossa.guns.plugin.SerializableAmmunition;
import de.cubbossa.guns.plugin.SerializableGun;
import de.cubbossa.guns.plugin.SerializableProjectile;
import de.cubbossa.menuframework.inventory.*;
import de.cubbossa.menuframework.inventory.implementations.AnvilMenu;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.stream.IntStream;

public class GunsEditor {

	public static Menu createGunsEditor() {
		ListEditorMenu<Gun> menu = new ListEditorMenu<>(Messages.E_GUNS_TITLE, 4, new ListMenuSupplier<Gun>() {
			@Override
			public Collection<Gun> getElements() {
				//TODO only sorts tagged name, like <yellow> after <red>
				return GunsHandler.getInstance().getGunsRegistry().values().stream()
						.sorted((o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.getName(), o2.getName()))
						.toList();
			}

			@Override
			public ItemStack getDisplayItem(Gun gun) {
				return gun.createWeaponStack();
			}
		});
		menu.setInfoItem(Messages.E_GUNS_INFO_NAME, Messages.E_GUNS_INFO_LORE);
		//TODO create duplicate
		menu.setDeleteHandler(Action.RIGHT, context -> GunsHandler.getInstance().getGunsRegistry().remove(context.getTarget().getKey()));
		return menu;
	}

	public static Menu createGunEditor(SerializableGun gun) {
		return null;
	}

	public static Menu createAmmunitionsEditor() {
		return null;
	}

	public static Menu createAmmunitionEditor(SerializableAmmunition ammunition) {
		return null;
	}

	public static Menu createAttachmentsEditor() {
		return null;
	}

	public static Menu createAttachmentEditor(Attachment attachment) {
		return null;
	}

	public static Menu createProjectilesEditor() {
		return null;
	}

	public static Menu createProjectileEditor(SerializableProjectile projectile) {
		return null;
	}

	public static Menu createImpactsEditor() {
		return null;
	}

	public static Menu createEntityImpactEditor(EntityImpact entityImpact) {
		return null;
	}

	public static Menu createBlockImpactEditor(BlockImpact blockImpact) {
		return null;
	}

	public static Menu createEffectsEditor() {
		return null;
	}

	public static MenuPreset<?> bottomRow(int row) {
		return buttonHandler -> {
			IntStream.range(row * 9, row * 9 + 9).forEach(v -> buttonHandler.addItem(v, Icon.EMPTY_DARK));
			buttonHandler.addItem(row * 9 + 5, Icon.EMPTY_DARK_RP);
		};
	}

	public static AnvilMenu newAnvilMenu(ComponentLike title, String suggestion) {
		return newAnvilMenu(title, suggestion, null);
	}

	public static <T> AnvilMenu newAnvilMenu(ComponentLike title, String suggestion, AnvilInputValidator<T> validator) {
		AnvilMenu menu = new AnvilMenu(title, suggestion);
		menu.addPreset(MenuPresets.back(1, Action.LEFT));
		menu.setClickHandler(0, AnvilMenu.WRITE, s -> {
			if (validator != null && !validator.getInputValidator().test(s.getTarget())) {
				menu.setItem(2, ItemStackUtils.createErrorItem(Messages.GUI_WARNING_NAME.asComponent(), validator.getErrorMessage().asComponents(TagResolver.resolver("format", Tag.inserting(validator.getRequiredFormat())))));
			} else {
				menu.setItem(2, Icon.ACCEPT_RP);
			}
			menu.refresh(2);
		});
		return menu;
	}
}
