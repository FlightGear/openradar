package de.knewcleus.radar.autolabel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HashMapOverlapModel<T> implements OverlapModel<T> {
	protected final Map<T, Set<T>> overlapMap=new HashMap<T, Set<T>>();

	@Override
	public boolean doesOverlap(T s1, T s2) {
		if (!overlapMap.containsKey(s1))
			return false;
		return overlapMap.get(s1).contains(s2);
	}
	
	@Override
	public Set<T> getOverlaps(T symbol) {
		if (!overlapMap.containsKey(symbol))
			return Collections.emptySet();
		return overlapMap.get(symbol);
	}
	
	@Override
	public void registerOverlap(T s1, T s2) {
		registerOverlapPair(s1, s2);
		registerOverlapPair(s2, s1);
	}
	
	protected void registerOverlapPair(T subject, T object) {
		Set<T> overlaps=getOrCreateOverlaps(subject);
		overlaps.add(object);
	}
	
	@Override
	public void unregisterObject(T object) {
		if (!overlapMap.containsKey(object))
			return;
		Set<T> overlaps=overlapMap.get(object);
		
		for (T overlap: overlaps) {
			assert(overlapMap.containsKey(overlap));
			overlapMap.get(overlap).remove(object);
		}
	}
	
	protected Set<T> getOrCreateOverlaps(T symbol) {
		if (overlapMap.containsKey(symbol))
			return overlapMap.get(symbol);
		Set<T> symbolOverlaps=new HashSet<T>();
		overlapMap.put(symbol, symbolOverlaps);
		return symbolOverlaps;
	}
}
