package de.cubbossa.guns.api;

public interface Impact<T> {

	void handleHit(T target);
}
