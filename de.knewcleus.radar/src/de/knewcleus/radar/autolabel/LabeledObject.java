package de.knewcleus.radar.autolabel;

import java.util.Set;

public interface LabeledObject extends BoundedSymbol {
	public Set<LabelCandidate> getLabelCandidates();
	public double getUnlabeledPenalty();
}
