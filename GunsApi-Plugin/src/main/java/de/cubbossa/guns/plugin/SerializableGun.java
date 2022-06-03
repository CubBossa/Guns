package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.effects.EffectPlayer;
import lombok.Getter;
import lombok.Setter;
import nbo.NBOSerializable;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
public class SerializableGun extends SimpleGun implements NBOSerializable {

	private String permission;
	private EffectPlayer muzzleEffect;
	private EffectPlayer rechargeEffect;
	private EffectPlayer noAmmoEffect;

	public SerializableGun(NamespacedKey key) {
		super(key);
		setUsePredicate(player -> permission == null || player.hasPermission(permission));
		setMuzzleFlashFactory(() -> muzzleEffect.clone());
		setRechargeEffectFactory(() -> rechargeEffect.clone());
		setNoAmmunitionEffectFactory(() -> noAmmoEffect.clone());
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("key", getKey().toString());
		map.put("item", getItemStack());
		map.put("name", getName());
		map.put("lore", getLore());
		map.put("permission", permission);
		map.put("valid_ammunition", getValidAmmunition());
		map.put("valid_attachments", getAttachments());
		map.put("muzzle_effect", muzzleEffect);
		map.put("recharge_effect", rechargeEffect);
		map.put("no_ammo_effect", noAmmoEffect);
		return map;
	}

	public static SerializableGun deserialize(Map<String, Object> map) {
		SerializableGun gun = new SerializableGun(NamespacedKey.fromString(getMapAttribute(map, "key", null)));
		gun.setItemStack(getMapAttribute(map, "item", new ItemStack(Material.STONE_HOE)));
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
}
