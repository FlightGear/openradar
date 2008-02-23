package de.knewcleus.radar.autolabel;

public class LabelOverlapModel<T> extends HashMapOverlapModel<T> {
	@Override
	public void registerOverlap(T s1, T s2) {
		if (s1 instanceof LabelCandidate && s2 instanceof LabelCandidate && ((LabelCandidate)s1).getAssociatedObject()==((LabelCandidate)s2).getAssociatedObject())
			return; // ignore overlaps between sibling labels
		super.registerOverlap(s1, s2);
	}
}
