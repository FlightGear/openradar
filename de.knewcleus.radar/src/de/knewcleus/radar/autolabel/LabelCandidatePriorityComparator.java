package de.knewcleus.radar.autolabel;

import java.util.Comparator;

public class LabelCandidatePriorityComparator implements Comparator<LabelCandidate> {
	protected final LabelCostModel costModel;

	public LabelCandidatePriorityComparator(LabelCostModel costModel) {
		this.costModel=costModel;
	}
	
	@Override
	public int compare(LabelCandidate o1, LabelCandidate o2) {
		double c1=costModel.getCandidateCost(o1);
		double c2=costModel.getCandidateCost(o2);
		
		return Double.compare(c2, c1);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LabelCandidatePriorityComparator))
			return false;
		LabelCandidatePriorityComparator other=(LabelCandidatePriorityComparator)obj;
		return other.costModel==costModel;
	}
}
