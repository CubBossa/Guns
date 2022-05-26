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
@NoArgsConstructor
@AllArgsConstructor
public class ParticlePlayer extends EffectPlayer {

	private Particle particle;
	private int amount;
	private Vector offset = new Vector(0, 0, 0);
	private Vector motion = new Vector(0, 0, 0);

	public ParticlePlayer(Particle particle, int amount, Vector offset) {
		this.particle = particle;
		this.amount = amount;
		this.offset = offset;
	}

	@Override public void play(Location location) {
		location = location.clone().subtract(0, .3f, 0);
		Vector motion = this.motion.clone().multiply(location.getDirection());
		location.getWorld().spawnParticle(particle, location.clone().add((Math.random() * 2 - 1) * offset.getX(), (Math.random() * 2 - 1) * offset.getY(), (Math.random() * 2 - 1) * offset.getZ()), amount, motion.getX(), motion.getY(), motion.getZ());
		super.play(location);
	}

	public ParticlePlayer withParticle(Particle particle) {
		this.particle = particle;
		return this;
	}

	public ParticlePlayer withAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public ParticlePlayer withOffset(Vector offset) {
		this.offset = offset;
		return this;
	}

	public ParticlePlayer withMotion(Vector motion) {
		this.motion = motion;
		return this;
	}
}
