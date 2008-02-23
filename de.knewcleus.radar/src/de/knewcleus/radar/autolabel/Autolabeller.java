package de.knewcleus.radar.autolabel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Autolabeller {
	protected final Set<LabeledObject> labeledObjects=new HashSet<LabeledObject>();
	protected final Set<ProtectedSymbol> protectedSymbols=new HashSet<ProtectedSymbol>();
	protected final Set<LabelCandidate> labelCandidates=new HashSet<LabelCandidate>();
	protected OverlapModel<BoundedSymbol> overlapModel;
	protected SymbolOverlapFinder<BoundedSymbol> overlapFinder;
	protected final Map<LabeledObject,LabelCandidate> currentLabelling=new HashMap<LabeledObject, LabelCandidate>();
	protected final Set<LabeledObject> conflictingLabels=new HashSet<LabeledObject>();
	protected final Random random=new Random();
	
	protected double currentTemperature;
	protected double currentCost;
	
	public void addLabeledObject(LabeledObject object) {
		labeledObjects.add(object);
		
		currentLabelling.put(object, selectRandomFromCollection(object.getLabelCandidates()));
	}
	
	public void addProtectedSymbol(ProtectedSymbol symbol) {
		protectedSymbols.add(symbol);
	}
	
	public void removeLabeledObject(LabeledObject object) {
		labeledObjects.remove(object);
		currentLabelling.remove(object);
	}
	
	public void removeProtectedSymbol(ProtectedSymbol symbol) {
		protectedSymbols.remove(symbol);
	}
	
	public int label(long deadlineMillis) {
		int changes=0;
		// T=-de/ln(1-p0)
		currentTemperature=-5.0/Math.log(1.0-2.0/3.0);
		
		overlapModel=new LabelOverlapModel<BoundedSymbol>();
		overlapFinder=new SymbolOverlapFinder<BoundedSymbol>(overlapModel);
		overlapFinder.addSymbols(protectedSymbols);
		overlapFinder.addSymbols(labeledObjects);
		
		for (LabeledObject object: labeledObjects) {
			overlapFinder.addSymbols(object.getLabelCandidates());
		}
		
		overlapFinder.run();
		
		conflictingLabels.clear();
		
		for (LabelCandidate candidate: currentLabelling.values()) {
			if (checkForConflict(candidate)) {
				conflictingLabels.add(candidate.getAssociatedObject());
			}
		}
		
		currentCost=calculateCost();
		
		while (System.currentTimeMillis()<deadlineMillis && conflictingLabels.size()>0) {
			if (stepLabel())
				changes++;
		}
		
		return changes;
	}
	
	private boolean stepLabel() {
		Set<LabeledObject> reassignCandidates=conflictingLabels;

		LabeledObject selectedObject=selectRandomFromCollection(reassignCandidates);
		if (selectedObject.getLabelCandidates().size()<2)
			return false;
		
		LabelCandidate currentCandidate=currentLabelling.get(selectedObject);
		LabelCandidate newCandidate=selectRandomFromCollection(selectedObject.getLabelCandidates());
		
		if (newCandidate==currentCandidate)
			return false;
		
		double newCost=currentCost-calculateCandidateCost(currentCandidate);
		
		currentLabelling.put(selectedObject,newCandidate);
		
		newCost+=calculateCandidateCost(newCandidate);

		if (newCost>currentCost) {
			double deltaCost=newCost-currentCost;
			double p=1.0-Math.exp(-deltaCost/currentTemperature);
			if (random.nextDouble()>p) {
				/* Undo this change */
				currentLabelling.put(selectedObject,currentCandidate);
				return false;
			}
		}
		
		/* Accept the change */
		currentCost=newCost;

		Set<LabeledObject> affectedOthers=new HashSet<LabeledObject>();
		Set<BoundedSymbol> affectedOverlaps=new HashSet<BoundedSymbol>();
		
		affectedOverlaps.addAll(overlapModel.getOverlaps(currentCandidate));
		affectedOverlaps.addAll(overlapModel.getOverlaps(newCandidate));
		
		for (BoundedSymbol overlap: affectedOverlaps) {
			if (overlap instanceof LabelCandidate) {
				LabelCandidate overlapCandidate=(LabelCandidate)overlap;
				
				if (currentLabelling.get(overlapCandidate.getAssociatedObject())==overlapCandidate) {
					affectedOthers.add(overlapCandidate.getAssociatedObject());
				}
			}
		}
		
		affectedOthers.add(selectedObject);
		conflictingLabels.removeAll(affectedOthers);
		
		for (LabeledObject labeledObject: affectedOthers) {
			LabelCandidate candidate=currentLabelling.get(labeledObject);
			if (checkForConflict(candidate)) {
				conflictingLabels.add(labeledObject);
			}
		}
		
		double factor=Math.pow(0.9,1.0/labeledObjects.size());
		currentTemperature*=factor;
		
		return true;
	}
	
	public OverlapModel<BoundedSymbol> getOverlapModel() {
		return overlapModel;
	}
	
	public Map<LabeledObject, LabelCandidate> getCurrentLabelling() {
		return Collections.unmodifiableMap(currentLabelling);
	}
	
	public Set<LabeledObject> getConflictingLabels() {
		return conflictingLabels;
	}
	
	public double getCurrentCost() {
		return currentCost;
	}
	
	public double getCurrentTemperature() {
		return currentTemperature;
	}
	
	private boolean checkForConflict(LabelCandidate candidate) {
		for (BoundedSymbol overlap: overlapModel.getOverlaps(candidate)) {
			if (overlap instanceof LabelCandidate) {
				/* In case of overlapping label candidates we need to check whether
				 * the candidate is in the current labelling. Only then we may count
				 * this as a conflict.
				 */
				LabelCandidate overlapCandidate=(LabelCandidate)overlap;
				if (currentLabelling.get(overlapCandidate.getAssociatedObject())==overlapCandidate) {
					return true;
				}
				continue;
			}
			return true;
		}
		
		return false;
	}
	
	private <T> T selectRandomFromCollection(Collection<T> collection) {
		int n=random.nextInt(collection.size());
		
		Iterator<T> iterator=collection.iterator();
		
		for (int i=0;i<n;i++) {
			iterator.next();
		}
		
		return iterator.next();
	}
	
	private double calculateCost() {
		double cost=0.0;
		
		for (LabelCandidate candidate: currentLabelling.values()) {
			cost+=calculateCandidateCost(candidate);
		}
		
		return cost;
	}
	
	private double calculateCandidateCost(LabelCandidate candidate) {
		double cost=0.0;
		
		cost+=candidate.getCost();
		
		for (BoundedSymbol overlap: overlapModel.getOverlaps(candidate)) {
			if (overlap instanceof LabelCandidate) {
				LabelCandidate overlapCandidate=(LabelCandidate)overlap;
				
				if (currentLabelling.get(overlapCandidate.getAssociatedObject())==overlapCandidate) {
					cost+=getOverlapAmount(candidate,overlap);
				}
			}
			if (overlap instanceof ProtectedSymbol) {
				ProtectedSymbol protectedSymbol=(ProtectedSymbol)overlap;
				cost+=protectedSymbol.getOverlapPenalty()*getOverlapAmount(candidate,overlap);
			}
		}
		
		return cost;
	}
	
	private double getOverlapAmount(BoundedSymbol s1, BoundedSymbol s2) {
		double otop,obottom,oleft,oright;
		
		otop=Math.max(s1.getTop(), s2.getTop());
		oleft=Math.max(s1.getLeft(), s2.getLeft());
		obottom=Math.min(s1.getBottom(), s2.getBottom());
		oright=Math.min(s1.getRight(), s2.getRight());
		
		if (oleft>oright || otop>obottom)
			return 0.0;
		return (oright-oleft)*(obottom-otop);
	}
}
