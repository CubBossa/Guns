package de.cubbossa.guns.plugin;

import de.cubbossa.guns.api.effects.SoundPlayer;
import de.tr7zw.nbtapi.NBTContainer;
import org.bukkit.Sound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

class NBTObjectSerializerTest {

	@Test
	public void testParseString() {
		NBTObjectSerializer serializer = new NBTObjectSerializer();
		Assertions.assertEquals(Map.of("yop", "Stein{bla{xy}}"), serializer.loadObjectStringsFromString("yop=Stein{bla{xy}}"));
		Assertions.assertEquals(Map.of("yop", "XYZ{bla'}xy{'}"), serializer.loadObjectStringsFromString("yop=XYZ{bla'}xy{'}"));
	}

	@Test
	public void testParenthesis() {
		NBTObjectSerializer serializer = new NBTObjectSerializer();
		Assertions.assertEquals(Map.of(0, 1), serializer.getBracketStartIndices("{}"));
		Assertions.assertEquals(Map.of(0, 3), serializer.getBracketStartIndices("{xy}"));
		Assertions.assertEquals(Map.of(1, 3), serializer.getBracketStartIndices("x{y}"));
		Assertions.assertEquals(Map.of(1, 3, 4, 5), serializer.getBracketStartIndices("x{y}{}"));
		Assertions.assertEquals(Map.of(1, 5), serializer.getBracketStartIndices("x{y{}}"));
		Assertions.assertEquals(Map.of(1, 7), serializer.getBracketStartIndices("x{y'}{'}"));
		Assertions.assertEquals(Map.of(1, 8), serializer.getBracketStartIndices("x{y#-}-#}"));
		Assertions.assertEquals(Map.of(1, 2), serializer.getBracketStartIndices("x{}y#-}#"));
		Assertions.assertEquals(Map.of(), serializer.getBracketStartIndices("#-xy}-#"));
	}

	@Test
	public void testFileRead() throws IOException {
		NBTObjectSerializer.registerConverter(new NBTObjectSerializer.Converter<>(
				"SoundPlayer",
				SoundPlayer.class,
				soundPlayer -> {
					NBTContainer container = new NBTContainer();
					container.setString("sound", soundPlayer.getSound().getKey().toString());
					container.setFloat("volume", soundPlayer.getVolume());
					container.setFloat("pitch", soundPlayer.getPitch());
					return container;
				},
				container -> {
					String soundKey = container.getString("sound");
					return new SoundPlayer(Sound.valueOf(soundKey), container.getFloat("volume"), container.getFloat("pitch"));
				}
		));

		NBTObjectSerializer serializer = new NBTObjectSerializer();
		var x = serializer.loadObjects(new File("C:/Users/leona/intellij-workspace/Guns/GunsApi-Plugin/src/main/resources/effects.txt"));
		x.forEach((string, o) -> System.out.println(string + "-> " + o));
	}

}