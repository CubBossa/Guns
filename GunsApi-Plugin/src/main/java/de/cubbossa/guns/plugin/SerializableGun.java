package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.Ammunition;
import de.cubbossa.guns.api.Gun;
import de.cubbossa.guns.api.GunAction;
import de.cubbossa.guns.api.GunsHandler;
import de.cubbossa.guns.api.attachments.Attachment;
import de.cubbossa.guns.api.context.*;
import de.cubbossa.guns.api.effects.EffectPlayer;
import de.cubbossa.guns.api.effects.SoundPlayer;
import lombok.Getter;
import lombok.Setter;
import nbo.NBOSerializable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;

@Getter
@Setter
public class SerializableGun implements Gun, NBOSerializable {

	private final NamespacedKey key;
	protected String permission;
	protected EffectPlayer muzzleEffect;
	protected EffectPlayer rechargeEffect;
	protected EffectPlayer noAmmoEffect;
	private ItemStack itemStack = new ItemStack(Material.STONE_HOE);
	private boolean useDamagableBar = true;
	private String name = "Just a Gun";
	private List<String> lore = new ArrayList<>();
	private Predicate<Player> usePredicate = p -> true;

	private List<Attachment> attachments = new ArrayList<>();
	private List<Ammunition> validAmmunition = new ArrayList<>();
	private Supplier<EffectPlayer> muzzleFlashFactory = () -> new SoundPlayer().withSound(Sound.BLOCK_STONE_BUTTON_CLICK_OFF);
	private Supplier<EffectPlayer> rechargeEffectFactory = EffectPlayer::new;
	private Supplier<EffectPlayer> noAmmunitionEffectFactory = EffectPlayer::new;
	private Map<GunAction<?>, ContextConsumer<?>> actionMap = new HashMap<>();

	public SerializableGun(NamespacedKey key) {
		this.key = key;

		addActionHandler(GunsHandler.ACTION_SHOOT, this::shoot);
		addActionHandler(GunsHandler.ACTION_HIT, this::hit);
		addActionHandler(GunsHandler.ACTION_RECHARGE, this::recharge);
	}

	public static SerializableGun deserialize(Map<String, Object> map) {
		SerializableGun gun = new SerializableGun(NamespacedKey.fromString(getMapAttribute(map, "key", null)));
		gun.setItemStack(getMapAttribute(map, "item", new ItemStack(Material.STONE_HOE)));
		gun.setUseDamagableBar(getMapAttribute(map, "use-damage-bar", true));
		gun.setName(getMapAttribute(map, "name", "Gun"));
		gun.setLore(getMapAttribute(map, "lore", new ArrayList<>()));
		gun.setPermission(getMapAttribute(map, "permission", null));
		gun.setValidAmmunition(getMapAttribute(map, "valid_ammunition", new ArrayList<>()));
		gun.setAttachments(getMapAttribute(map, "valid_attachments", new ArrayList<>()));
		gun.setMuzzleEffect(getMapAttribute(map, "muzzle_effect", new EffectPlayer()));
		gun.setRechargeEffect(getMapAttribute(map, "recharge_effect", new EffectPlayer()));
		gun.setNoAmmoEffect(getMapAttribute(map, "no_ammo_effect", new EffectPlayer()));
		return gun;
	}

	private static <T> T getMapAttribute(Map<String, Object> map, String key, T ifAbsent) {
		if (!map.containsKey(key)) {
			if (ifAbsent != null) {
				return ifAbsent;
			}
			throw new RuntimeException("Could not deserialize gun, map does not contain attribute '" + key + "'.");
		}
		try {
			T var = (T) map.getOrDefault(key, ifAbsent);
			return var == null ? ifAbsent : var;
		} catch (ClassCastException e) {
			throw new RuntimeException("Could not deserialize gun, map does not contain attribute '" + key + "' of the correct type: " + ifAbsent.getClass());
		}
	}

	public void addAttachment(Attachment attachment) {
		this.attachments.add(attachment);
	}

	public void removeAttachment(Attachment attachment) {
		this.attachments.remove(attachment);
	}

	public Ammunition getFirstFittingAmmunition(Player player) {
		for (Ammunition ammunition : getValidAmmunition()) {
			//TODO bah
			if (ammunition.getCount(player) >= ammunition.getBulletCount()) {
				return ammunition;
			}
		}
		return null;
	}

	public Map.Entry<Ammunition, Integer> getAmmunitionCharged(ItemStack stack) {
		return GunsHandler.getInstance().getAmmunition(stack);
	}

	public void setAmmunitionCharged(ItemStack stack, Ammunition ammunition, int amount) {
		GunsHandler.getInstance().setAmmunition(stack, ammunition, amount);
	}

	public int getAmmunitionUncharged(Player player, Ammunition ammunition) {
		return ammunition.getCount(player);
	}

	public void addValidAmmunition(Ammunition ammunition) {
		this.validAmmunition.add(ammunition);
	}

	public void removeValidAmmunition(Ammunition ammunition) {
		this.validAmmunition.remove(ammunition);
	}

	public Vector getRecoil(Vector direction) {
		return direction.multiply(-.1);
	}

	@Override
	public <C extends GunActionContext> void addActionHandler(GunAction<C> action, ContextConsumer<C> handler) {
		actionMap.put(action, handler);
	}

	public <C extends GunActionContext> void perform(GunAction<C> action, C context) throws Throwable {
		ContextConsumer<C> handler = (ContextConsumer<C>) actionMap.get(action);
		if (handler == null) {
			return;
		}
		handler.accept(context);
	}

	public void hit(HitContext context) {

	}

	public void recharge(RechargeContext context) {

		EffectPlayer effectPlayer = getRechargeEffectFactory().get();
		context.setAmmunition(getFirstFittingAmmunition(context.getPlayer()));

		context.getCancellable().setCancelled(true);
		for (Attachment attachment : attachments) {
			try {
				attachment.perform(GunsHandler.ACTION_RECHARGE, context);
			} catch (Throwable t) {
				GunsHandler.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Could not perform guns attachment action for gun '" + key.toString() + "'.", t);
			}
		}
		if (context.isCancelled()) {
			return;
		}
		if (context.getAmmunition() == null) {
			return;
		}
		context.getAmmunition().removeCount(context.getPlayer(), context.getAmmunition().getBulletCount());
		GunsHandler.getInstance().setAmmunition(context.getStack(), context.getAmmunition(), context.getAmmunition().getBulletCount());
		GunsHandler.getInstance().updateItemStack(context.getStack(), this, context.getAmmunition(), context.getAmmunition().getBulletCount());
		effectPlayer.play(context.getPlayer().getLocation());
	}

	public void shoot(ShootContext context) {

		context.getCancellable().setCancelled(true);

		var pair = getAmmunitionCharged(context.getStack());
		// No ammunition charged
		if (pair == null) {
			noAmmunitionEffectFactory.get().play(context.getPlayer().getEyeLocation());
			return;
		}
		Ammunition ammunition = pair.getKey();

		// Prepare shot
		EffectPlayer flash = getMuzzleFlashFactory().get();
		ProjectileContext projectile = new ProjectileContext(ammunition.getProjectile(), context.getPlayer());

		context.setMuzzleFlash(flash);
		context.setProjectile(projectile);
		context.setRecoil(getRecoil(context.getPlayer().getLocation().getDirection()));
		context.setAmmunitionCosts(1);

		// Process
		for (Attachment attachment : attachments) {
			try {
				attachment.perform(GunsHandler.ACTION_SHOOT, context);
			} catch (Throwable t) {
				GunsHandler.getInstance().getPlugin().getLogger().log(Level.SEVERE, "Could not perform guns attachment action for gun '" + key.toString() + "'.", t);
			}
		}
		if (context.isCancelled()) {
			return;
		}

		Player player = context.getPlayer();

		// Not enough ammunition charged
		if (pair.getValue() < context.getAmmunitionCosts()) {
			noAmmunitionEffectFactory.get().play(context.getPlayer().getEyeLocation());
			return;
		}
		GunsHandler.getInstance().setAmmunition(context.getStack(), ammunition, pair.getValue() - context.getAmmunitionCosts());
		GunsHandler.getInstance().updateItemStack(context.getStack(), this, ammunition, pair.getValue() - context.getAmmunitionCosts());
		player.getInventory().setItemInMainHand(context.getStack());

		// Apply shot
		player.setVelocity(player.getVelocity().add(context.getRecoil()));
		flash.play(player.getEyeLocation());
		ammunition.getProjectile().create(projectile);
	}

	public void updateWeaponStack(ItemStack stack) {
		Map.Entry<Ammunition, Integer> pair = getAmmunitionCharged(stack);
		if (pair == null) {
			return;
		}
		GunsHandler.getInstance().updateItemStack(stack, this, pair.getKey(), pair.getValue());
	}

	public ItemStack createWeaponStack() {
		ItemStack stack = itemStack.clone();
		ItemMeta meta = stack.getItemMeta();

		if (meta instanceof Damageable damageable) {
			damageable.setDamage(stack.getType().getMaxDurability() - 1);
		}
		meta.setDisplayName(GunsHandler.LEGACY_SERIALIZER.serialize(GunsHandler.getInstance().deserializeLine(getName())));
		meta.setLore(GunsHandler.getInstance().getLore(this));
		meta.getPersistentDataContainer().set(new NamespacedKey(GunsAPI.getInstance(), "guns-id"), PersistentDataType.STRING, UUID.randomUUID().toString());
		stack.setItemMeta(meta);

		return GunsHandler.getInstance().setIdentifier(stack, this);
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key", key.toString());
		map.put("item", itemStack);
		map.put("use-damage-bar", useDamagableBar);
		map.put("name", name);
		map.put("lore", lore);
		map.put("permission", permission);
		map.put("valid_ammunition", getValidAmmunition());
		map.put("valid_attachments", getAttachments());
		map.put("muzzle_effect", muzzleEffect);
		map.put("recharge_effect", rechargeEffect);
		map.put("no_ammo_effect", noAmmoEffect);
		return map;
	}
}
