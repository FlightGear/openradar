package de.knewcleus.radar.autolabel;

import java.util.Set;

public interface OverlapModel<T> {
	public void registerOverlap(T s1, T s2);
	public void unregisterObject(T object);
	public boolean doesOverlap(T s1, T s2);
	public Set<T> getOverlaps(T symbol);
}
