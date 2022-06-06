package de.cubbossa.guns.api;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Iterator;

public class HashedRegistry<T extends Keyed> extends HashMap<NamespacedKey, T> implements Registry<T> {

	public void put(T keyed) {
		super.put(keyed.getKey(), keyed);
	}

	@Nullable
	@Override
	public T get(@NotNull NamespacedKey namespacedKey) {
		return super.get(namespacedKey);
	}

	@NotNull
	@Override
	public Iterator<T> iterator() {
		return values().iterator();
	}
}
