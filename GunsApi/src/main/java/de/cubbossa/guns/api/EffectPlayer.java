package de.cubbossa.guns.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

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

    public EffectPlayer withEffect(int delay, EffectPlayer effect) {
        effects.put(effect, delay);
        return this;
    }

    public void play(Location location) {
        for (Map.Entry<EffectPlayer, Integer> entry : effects.entrySet()) {
            Bukkit.getScheduler().runTaskLater(GunsHandler.getInstance().getPlugin(), () -> entry.getKey().play(location), entry.getValue());
        }
    }
}
