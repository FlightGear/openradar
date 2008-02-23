package de.knewcleus.radar.autolabel;

import java.util.Comparator;

class SweepLineEventPriorityComparator implements Comparator<SweepLineEvent> {
	@Override
	public int compare(SweepLineEvent o1, SweepLineEvent o2) {
		return Double.compare(o1.getPosition(),o2.getPosition());
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof SweepLineEventPriorityComparator);
	}
}
