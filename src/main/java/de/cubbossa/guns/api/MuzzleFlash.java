package de.cubbossa.guns.api;

import org.bukkit.Location;

public interface MuzzleFlash {

	ParticlePlayer getParticlePlayer();

	void setParticlePlayer(ParticlePlayer particlePlayer);

	SoundPlayer getSoundPlayer();

	void setSoundPlayer(SoundPlayer soundPlayer);

	void play(Location location);
}
