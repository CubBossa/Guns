package de.cubbossa.guns.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@Setter
public class SoundPlayer extends EffectPlayer {

    private Sound sound = Sound.ENTITY_EGG_THROW;
    private float volume = 1f;
    private float pitch = 1f;

    public SoundPlayer withSound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public SoundPlayer withVolume(float volume) {
        this.volume = volume;
        return this;
    }

    public SoundPlayer withPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public void play(Location location) {
        for (Player player : location.getWorld().getPlayers()) {
            player.playSound(location, sound, volume, pitch);
        }
        super.play(location);
    }
}
