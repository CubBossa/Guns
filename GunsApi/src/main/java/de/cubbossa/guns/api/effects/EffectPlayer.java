package de.cubbossa.guns.api.effects;

import de.cubbossa.guns.api.GunsHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class EffectPlayer {

    private final HashMap<EffectPlayer, Integer> effects;

    public EffectPlayer() {
        this.effects = new HashMap<>();
    }

    public void addEffect(int delay, EffectPlayer effect) {
        effects.put(effect, delay);
    }

    public void removeEffect(EffectPlayer effect) {
        effects.remove(effect);
    }

    public EffectPlayer withEffect(EffectPlayer effect) {
        return withEffect(0, effect);
    }

    public EffectPlayer withEffect(int delay, EffectPlayer effect) {
        effects.put(effect, delay);
        return this;
    }

    public void play(Location location) {
        for (Map.Entry<EffectPlayer, Integer> entry : effects.entrySet()) {
            if (entry.getValue() > 0) {
                Bukkit.getScheduler().runTaskLater(GunsHandler.getInstance().getPlugin(), () -> entry.getKey().play(location), entry.getValue());
            } else {
                entry.getKey().play(location);
            }
        }
    }
}
