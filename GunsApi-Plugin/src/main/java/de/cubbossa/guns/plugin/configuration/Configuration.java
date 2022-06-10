package de.cubbossa.guns.plugin.configuration;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class Configuration {

	// Config fields

	@ConfigValue(path = "lang.client-language", comments = """
			If messages should automatically be translated to client language, if a translation file
			for the provided client language exists.""")
	private boolean clientLanguage = false;
	@ConfigValue(path = "lang.fallback-language", comments = """
			The language that automatically will be used for players with unknown client locale.""")
	private String fallbackLanguage = "en_US";

	@ConfigValue(path = "gui.confirm-deletion", comments = """
			If editor menus require administrators to confirm the deletion of guns and other objects.""")
	private boolean confirmDeletion = false;

	@ConfigValue(path = "guns.main-file", comments = """
            The provided NBO file will be read and interpreted when the plugin loads.
            If you want to use multiple NBO files, use the include syntax.
            More information on how to use NBO can be found here:
            https://github.com/CubBossa/NBOParser/blob/main/FIRST_USERS.md""")
	private String nboFile = "guns.nbo";

	// Load and save

	public void saveToFile(File file) throws IOException, IllegalAccessException {

		if (!file.exists()) {
			if (!file.mkdirs() || !file.createNewFile()) {
				throw new RuntimeException("Unexpected error while saving config file.");
			}
		}
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		List<Field> fields = Arrays.stream(Configuration.class.getFields())
				.filter(field -> field.isAnnotationPresent(ConfigValue.class))
				.toList();

		for (Field field : fields) {
			ConfigValue meta = field.getAnnotation(ConfigValue.class);
			cfg.setComments(meta.path(), Arrays.stream(meta.comments())
					.map(s -> s.split("\n"))
					.flatMap(Arrays::stream)
					.toList());
			cfg.set(meta.path(), field.get(this));
		}

		cfg.save(file);
	}

	public static Configuration loadFromFile(File file) throws IllegalAccessException, IOException {
		if (!file.exists()) {
			Configuration configuration = new Configuration();
			configuration.saveToFile(file);
			return configuration;
		}

		Configuration configuration = new Configuration();
		YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

		List<Field> fields = Arrays.stream(Configuration.class.getFields())
				.filter(field -> field.isAnnotationPresent(ConfigValue.class))
				.toList();

		for (Field field : fields) {
			ConfigValue meta = field.getAnnotation(ConfigValue.class);
			if (cfg.isSet(meta.path())) {
				field.set(configuration, cfg.get(meta.path()));
			}
		}

		return configuration;
	}
}
