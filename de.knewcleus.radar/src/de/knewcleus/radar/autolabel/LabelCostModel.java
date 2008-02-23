package de.knewcleus.radar.autolabel;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class LabelCostModel {
	protected final OverlapModel<BoundedSymbol> overlapModel;
	protected final Set<LabeledObject> labeledObjects=new HashSet<LabeledObject>();
	protected final Map<LabelCandidate, Double> perCandidateCostMap=new HashMap<LabelCandidate, Double>();
	protected final Map<LabeledObject, Integer> candidatesRemaining=new HashMap<LabeledObject, Integer>();
	protected int multiCandidateObjectCount;
	protected final Random random=new Random();
	
	public LabelCostModel(OverlapModel<BoundedSymbol> overlapModel) {
		this.overlapModel=overlapModel;
	}
	
	public int getMultiCandidateObjectCount() {
		return multiCandidateObjectCount;
	}

	public void registerLabeledObject(LabeledObject labeledObject) {
		labeledObjects.add(labeledObject);
	}
	
	public void prepare() {
		for (LabeledObject labeledObject: labeledObjects) {
			Set<LabelCandidate> labelCandidates=labeledObject.getLabelCandidates();
			
			candidatesRemaining.put(labeledObject, labelCandidates.size());
			if (labelCandidates.size()>1)
				multiCandidateObjectCount++;
			double perObjectCost=getPerObjectCost(labeledObject);
			
			for (LabelCandidate candidate: labelCandidates) {
				perCandidateCostMap.put(candidate,perObjectCost+getBasicCandidateCost(candidate));
			}
		}
	}
	
	public void removeCandidate(LabelCandidate candidate, Set<LabelCandidate> affectedCandidates) {
		LabeledObject labeledObject=candidate.getAssociatedObject();
		
		int remaining=candidatesRemaining.get(labeledObject)-1;
		candidatesRemaining.put(labeledObject,remaining);
		
		if (remaining==1)
			multiCandidateObjectCount--;

		Set<BoundedSymbol> overlaps=overlapModel.getOverlaps(candidate);
		perCandidateCostMap.remove(candidate);
		overlapModel.unregisterObject(candidate);
		
		for (LabelCandidate sibling: labeledObject.getLabelCandidates()) {
			if (!perCandidateCostMap.containsKey(sibling))
				continue;
			affectedCandidates.add(sibling);
		}
		
		/* Then process all overlapped candidates */
		for (BoundedSymbol overlap: overlaps) {
			if (!(overlap instanceof LabelCandidate))
				continue;
			LabelCandidate overlapCandidate=(LabelCandidate)overlap;
			affectedCandidates.add(overlapCandidate);
		}
	}
	
	public void updateCandidateCosts(Set<LabelCandidate> candidates) {
		for (LabelCandidate candidate: candidates) {
			LabeledObject object=candidate.getAssociatedObject();
			
			perCandidateCostMap.put(candidate, getPerObjectCost(object)+getBasicCandidateCost(candidate));
		}
	}
	
	public double getCandidateCost(LabelCandidate candidate) {
		Double cost=perCandidateCostMap.get(candidate);
		if (cost==null)
			return 0.0;
		return cost;
	}
	
	public double getMaximumCost() {
		return Collections.max(perCandidateCostMap.values());
	}
	
	public double getMinimumCost() {
		return Collections.min(perCandidateCostMap.values());
	}
	
	protected double getPerObjectCost(LabeledObject object) {
		Integer remaining=candidatesRemaining.get(object);
		if (remaining==null)
			return 0.0;
		return remaining*object.getUnlabeledPenalty();
	}
	
	protected double getBasicCandidateCost(LabelCandidate candidate) {
		double cost=candidate.getCost();
		
		for (BoundedSymbol overlap: overlapModel.getOverlaps(candidate)) {
			cost+=getOverlapCost(candidate, overlap);
		}
		
		return cost;
	}
	
	protected double getOverlapCost(LabelCandidate subject, BoundedSymbol object) {
		if (object instanceof LabelCandidate) {
			LabelCandidate objCand=(LabelCandidate)object;
			
			if (subject.getAssociatedObject()==objCand.getAssociatedObject()) {
				// Overlaps between sibling label candidates cost nothing, as we will reduce all objects to at most one label
				return 0.0;
			}
		}
		
		double costDelta=getOverlapAmount(subject, object);
		
		// Add a penalty if subject overlaps a protected object
		if (object instanceof ProtectedSymbol) {
			ProtectedSymbol protectedSymbol=(ProtectedSymbol)object;
			costDelta*=protectedSymbol.getOverlapPenalty();
		}
		
		return costDelta;
	}
	
	protected double getOverlapAmount(BoundedSymbol s1, BoundedSymbol s2) {
		double overlapTop,overlapBottom,overlapRight,overlapLeft;
		
		overlapTop=Math.max(s1.getTop(), s2.getTop());
		overlapBottom=Math.min(s1.getBottom(), s2.getBottom());
		overlapLeft=Math.max(s1.getLeft(), s2.getLeft());
		overlapRight=Math.min(s1.getRight(), s2.getRight());
		
		if (overlapTop>overlapBottom || overlapLeft>overlapRight)
			return 0.0;
		
		return (overlapBottom-overlapTop)*(overlapRight-overlapLeft);
	}
}
