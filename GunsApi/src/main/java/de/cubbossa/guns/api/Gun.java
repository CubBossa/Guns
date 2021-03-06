package de.cubbossa.guns.api;

import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.context.ContextConsumer;
import de.cubbossa.guns.api.context.GunActionContext;
import de.cubbossa.guns.api.effects.EffectPlayer;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Gun extends Keyed {

	ItemStack getItemStack();

	void setItemStack(ItemStack stack);

	String getName();

	void setName(String name);

	List<String> getLore();

	void setLore(List<String> lore);

	List<Attachment> getAttachments();

	void addAttachment(Attachment attachment);

	void removeAttachment(Attachment attachment);

	List<Ammunition> getValidAmmunition();

	Ammunition getFirstFittingAmmunition(Player player);

	Map.Entry<Ammunition, Integer> getAmmunitionCharged(ItemStack stack);

	void setAmmunitionCharged(ItemStack stack, Ammunition ammunition, int amount);

	int getAmmunitionUncharged(Player player, Ammunition ammunition);

	void addValidAmmunition(Ammunition ammunition);

	void removeValidAmmunition(Ammunition ammunition);

	Supplier<EffectPlayer> getNoAmmunitionEffectFactory();

	void setNoAmmunitionEffectFactory(Supplier<EffectPlayer> player);

	Supplier<EffectPlayer> getMuzzleFlashFactory();

	void setMuzzleFlashFactory(Supplier<EffectPlayer> factory);

	Supplier<EffectPlayer> getRechargeEffectFactory();

	void setRechargeEffectFactory(Supplier<EffectPlayer> effect);

	Vector getRecoil(Vector direction);

	Predicate<Player> getUsePredicate();

	void setUsePredicate(Predicate<Player> usePredicate);

	<C extends GunActionContext> void addActionHandler(GunAction<C> action, ContextConsumer<C> handler);

	<C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable;

	ItemStack createWeaponStack();
}
