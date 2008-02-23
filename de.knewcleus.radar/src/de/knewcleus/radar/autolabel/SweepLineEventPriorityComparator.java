package de.knewcleus.radar.autolabel;

import java.util.Comparator;

class SweepLineEventPriorityComparator<T> implements Comparator<SweepLineEvent<T>> {
	@Override
	public int compare(SweepLineEvent<T> o1, SweepLineEvent<T> o2) {
		return Double.compare(o1.getPosition(),o2.getPosition());
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SweepLineEventPriorityComparator);
	}
}
