package de.cubbossa.guns.api;

import de.cubbossa.guns.api.context.GunActionContext;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.NamespacedKey;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GunAction<C extends GunActionContext> {

	private final NamespacedKey key;

	@Override public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof GunAction)) {
			return false;
		}

		GunAction<?> gunAction = (GunAction<?>) o;

		return key.equals(gunAction.key);
	}

	@Override public int hashCode() {
		return key.hashCode();
	}
}
