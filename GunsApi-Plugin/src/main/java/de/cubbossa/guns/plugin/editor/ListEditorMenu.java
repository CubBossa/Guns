package de.cubbossa.guns.plugin.editor;

import de.cubbossa.guns.plugin.GunsAPI;
import de.cubbossa.menuframework.inventory.Action;
import de.cubbossa.menuframework.inventory.Button;
import de.cubbossa.menuframework.inventory.ListMenuSupplier;
import de.cubbossa.menuframework.inventory.MenuPresets;
import de.cubbossa.menuframework.inventory.context.ClickContext;
import de.cubbossa.menuframework.inventory.context.ContextConsumer;
import de.cubbossa.menuframework.inventory.context.TargetContext;
import de.cubbossa.menuframework.inventory.implementations.AnvilMenu;
import de.cubbossa.menuframework.inventory.implementations.ListMenu;
import de.cubbossa.translations.Message;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class ListEditorMenu<T> extends ListMenu {

	@Getter
	@Setter
	private ListMenuSupplier<T> supplier;

	private final Map<Action<?>, ContextConsumer<TargetContext<T>>> clickHandler;
	@Getter
	@Setter
	private BiFunction<T, ItemStack, ItemStack> itemModifier = (t, stack) -> stack;

	public ListEditorMenu(ComponentLike title, int rows, ListMenuSupplier<T> supplier) {
		super(title, rows, IntStream.range(0, (rows - 1) * 9).toArray());
		this.supplier = supplier;
		this.clickHandler = new HashMap<>();

		addPreset(GunsEditor.bottomRow(rows - 1));
		addPreset(MenuPresets.back(rows - 1, 8, Action.LEFT));
		addPreset(MenuPresets.paginationRow(rows - 1, 0, 1, false, Action.LEFT));
	}

	public void refreshElements() {
		clearListEntries();
		supplier.getElements().forEach(t -> {
			Map<Action<?>, ContextConsumer<? extends TargetContext<?>>> map = new HashMap<>();
			for (Map.Entry<Action<?>, ContextConsumer<TargetContext<T>>> entry : clickHandler.entrySet()) {
				map.put(entry.getKey(), c -> {
					var context = new TargetContext<>(c.getPlayer(), c.getMenu(), c.getSlot(), new Action<>(), c.isCancelled(), t);
					entry.getValue().accept(context);
					c.setCancelled(context.isCancelled());
				});
			}
			addListEntry(Button.builder().withItemStack(itemModifier.apply(t, supplier.getDisplayItem(t))).withClickHandler(map));
		});
		refresh(getListSlots());
	}

	public void setInfoItem(Message name, Message lore) {
		addPreset(applier -> applier.addItem((getRows() - 1) * 9 + 4, ItemStackUtils.createInfoItem(name, lore)));
	}

	public void setDeleteHandler(Action<?> action, ContextConsumer<TargetContext<T>> clickHandler) {
		ContextConsumer<TargetContext<T>> extended = c -> {
			clickHandler.accept(c);
			refreshElements();
		};
		this.clickHandler.put(action, extended);
	}

	public void setDeleteHandler(Message confirmTitle, Action<?> action, ContextConsumer<TargetContext<T>> clickHandler) {
		ContextConsumer<TargetContext<T>> extended = c -> {
			if (GunsAPI.getInstance().getConfiguration().isConfirmDeletion()) {
				openSubMenu(c.getPlayer(), () -> {
					ConfirmMenu m = new ConfirmMenu(confirmTitle);
					m.setDenyHandler(cl -> m.openPreviousMenu(cl.getPlayer()));
					m.setAcceptHandler(cl -> {
						clickHandler.accept(c);
						m.openPreviousMenu(cl.getPlayer());
						refreshElements();
					});
					return m;
				});
			} else {
				clickHandler.accept(c);
				refreshElements();
			}
		};
		this.clickHandler.put(action, extended);
	}

	public void setDuplicateHandler(Action<?> action, ContextConsumer<TargetContext<T>> clickHandler) {
		this.clickHandler.put(action, tTargetContext -> {
			clickHandler.accept(tTargetContext);
			refreshElements();
		});
	}

	public void setClickHandler(Action<?> action, ContextConsumer<TargetContext<T>> clickHandler) {
		this.clickHandler.put(action, tTargetContext -> {
			clickHandler.accept(tTargetContext);
			refreshElements();
		});
	}

	public void setNewHandler(Message name, Message lore, ContextConsumer<ClickContext> clickHandler) {
		addPreset(presetApplier -> {
			presetApplier.addItem((getRows() - 1) * 9 + 7, ItemStackUtils.createItemStack(Material.EMERALD, name, lore));
			presetApplier.addClickHandler((getRows() - 1) * 9 + 7, Action.LEFT, clickHandler);
		});
	}

	public void setNewHandlerStringInput(Message name, Message lore, Message title, String suggestion, ContextConsumer<TargetContext<String>> clickHandler) {
		setNewHandlerStringInput(name, lore, title, suggestion, null, clickHandler);
	}

	public void setNewHandlerStringInput(Message name, Message lore, Message title, String suggestion, AnvilInputValidator<T> validator, ContextConsumer<TargetContext<String>> clickHandler) {
		addPreset(presetApplier -> {
			presetApplier.addItem((getRows() - 1) * 9 + 7, ItemStackUtils.createItemStack(Material.EMERALD, name, lore));
			presetApplier.addClickHandler((getRows() - 1) * 9 + 7, Action.LEFT, c -> openSubMenu(c.getPlayer(), () -> {
				AnvilMenu m = GunsEditor.newAnvilMenu(title, suggestion, validator);
				m.setOutputClickHandler(AnvilMenu.CONFIRM, s -> {
					if (validator != null && !validator.getInputValidator().test(s.getTarget())) {
						GunsAPI.SOUND_DECLINE.accept(s.getPlayer());
						return;
					}
					m.openPreviousMenu(s.getPlayer());
					clickHandler.accept(s);
					refreshElements();
				});
				return m;
			}));
		});
	}

	@Override
	public void openSync(Player viewer, ViewMode viewMode) {
		refreshElements();
		super.openSync(viewer, viewMode);
	}
}
