package de.cubbossa.guns.plugin.handler;

import de.cubbossa.guns.api.effects.SoundPlayer;
import org.bukkit.Sound;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

class EffectsHandlerTest {

	@org.junit.jupiter.api.Test
	void deserialize() throws ParseException {
		new EffectsHandler().registerEffect("lol", new SoundPlayer(Sound.ENTITY_LEASH_KNOT_PLACE, 1f, 3.9817f));

	}

	@org.junit.jupiter.api.Test
	void serialize() {
	}
}