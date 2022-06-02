package de.cubbossa.guns.api.effects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Effect;
import org.bukkit.Location;

import java.util.LinkedHashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class WorldEffectPlayer extends EffectPlayer {

	private Effect effect;
	private Object data;

	public WorldEffectPlayer withEffect(Effect effect) {
		this.effect = effect;
		return this;
	}

	public <T> WorldEffectPlayer withData(T data) {
		this.data = data;
		return this;
	}

	public void play(Location location) {
		location.getWorld().playEffect(location, effect, data);
		super.play(location);
	}

	public Map<String, Object> serialize() {
		Map<String, Object> ret = new LinkedHashMap<>();
		ret.put("effect", effect.toString());
		if (data != null) {
			ret.put("data", data);
		}
		var subEffects = super.serialize();
		if (!subEffects.isEmpty()) {
			ret.put("effects", subEffects);
		}
		return ret;
	}

	public static WorldEffectPlayer deserialize(Map<String, Object> values) {
		Effect e = (Effect) values.getOrDefault("effect", Effect.ANVIL_BREAK);
		var ret = new WorldEffectPlayer(e, values.getOrDefault("data", null));
		EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
		return ret;
	}
}
