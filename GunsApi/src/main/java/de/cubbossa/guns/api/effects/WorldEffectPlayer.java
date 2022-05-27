package de.cubbossa.guns.api.effects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Effect;
import org.bukkit.Location;

@RequiredArgsConstructor
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
}
