package de.cubbossa.guns.api;

import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.context.ContextConsumer;
import de.cubbossa.guns.api.context.GunActionContext;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Gun {

	NamespacedKey getKey();

	ItemStack getItemStack();

	void setItemStack(ItemStack stack);

	ComponentLike getName();

	void setName(ComponentLike name);

	List<ComponentLike> getLore();

	void setLore(List<ComponentLike> lore);

	List<Attachment> getAttachments();

	void addAttachment(Attachment attachment);

	void removeAttachment(Attachment attachment);

	List<Ammunition> getAmmunition();

	Ammunition getFirstFittingAmmunition();

	int getAmmunitionCharged(Player player);

	void setAmmunitionCharged(Player player, int amount);

	Ammunition getAmmunitionTypeCharged(Player player);

	void setAmmunitionTypeCharged(Player player, Ammunition ammunition);

	int getAmmunitionUncharged(Player player, Ammunition ammunition);

	void addAmmunition(Ammunition ammunition);

	void removeAmmunition(Ammunition ammunition);

	Supplier<EffectPlayer> getMuzzleFlashFactory();

	void setMuzzleFlashFactory(Supplier<EffectPlayer> factory);

	Supplier<GunProjectile> getProjectileFactory();

	void setProjectileFactory(Supplier<GunProjectile> factory);

	Supplier<EffectPlayer> getRechargeEffectFactory();

	void setRechargeEffectFactory(Supplier<EffectPlayer> effect);

	Vector getRecoil(Vector direction);

	Predicate<Player> getUsePredicate();

	void setUsePredicate(Predicate<Player> usePredicate);

	<C extends GunActionContext> void addActionHandler(GunAction<C> action, ContextConsumer<C> handler);

	<C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable;

	ItemStack createWeaponStack();
}
