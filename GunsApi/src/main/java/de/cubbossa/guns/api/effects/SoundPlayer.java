package de.cubbossa.guns.api.effects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    public Map<String, Object> serialize() {
        Map<String, Object> ret = new LinkedHashMap<>();
        ret.put("sound", sound.toString());
        ret.put("volume", volume);
        ret.put("pitch", pitch);
        var subEffects = super.serialize();
        if (!subEffects.isEmpty()) {
            ret.put("effects", subEffects);
        }
        return ret;
    }

    public static SoundPlayer deserialize(Map<String, Object> values) {
        var ret = new SoundPlayer((Sound) values.getOrDefault("sound", Sound.ENTITY_VILLAGER_NO),
                (Float) values.getOrDefault("volume", 1),
                (Float) values.getOrDefault("pitch", 1));
        EffectPlayer.deserialize(values).getEffectPlayers(false).forEach((key, value) -> ret.addEffect(value, key));
        return ret;
    }
}
