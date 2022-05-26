package de.cubbossa.guns.api.effects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ParticleLinePlayer extends ParticlePlayer {

	private float length;
	private float distance;

	public ParticleLinePlayer(Particle particle, int amount, Vector offset, float length, float distance) {
		super(particle, amount, offset);
		this.length = length;
		this.distance = distance;
	}

	public ParticleLinePlayer withLength(float length) {
		this.length = length;
		return this;
	}

	public ParticleLinePlayer withDistance(float distance) {
		this.distance = distance;
		return this;
	}

	@Override public void play(Location location) {
		for(float i = 0; i < length; i += distance) {
			super.play(location.clone().add(location.getDirection().normalize().multiply(i)));
		}
	}
}
