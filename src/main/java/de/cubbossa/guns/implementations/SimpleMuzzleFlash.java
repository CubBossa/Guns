package de.cubbossa.guns.implementations;

import de.cubbossa.guns.api.MuzzleFlash;
import de.cubbossa.guns.api.ParticlePlayer;
import de.cubbossa.guns.api.SoundPlayer;
import org.bukkit.Location;

public class SimpleMuzzleFlash implements MuzzleFlash {

	@Override public ParticlePlayer getParticlePlayer() {
		return null;
	}

	@Override public void setParticlePlayer(ParticlePlayer particlePlayer) {

	}

	@Override public SoundPlayer getSoundPlayer() {
		return null;
	}

	@Override public void setSoundPlayer(SoundPlayer soundPlayer) {

	}

	@Override public void play(Location location) {

	}
}
