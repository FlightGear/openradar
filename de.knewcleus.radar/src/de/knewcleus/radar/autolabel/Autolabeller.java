package de.knewcleus.radar.autolabel;

import java.util.Collection;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class Autolabeller {
	protected final Set<LabeledObject> labeledObjects=new HashSet<LabeledObject>();
	protected final Set<LabelCandidate> labelCandidates=new HashSet<LabelCandidate>();
	protected final OverlapModel<BoundedSymbol> overlapModel=new HashMapOverlapModel<BoundedSymbol>();
	protected final LabelCostModel costModel=new LabelCostModel(overlapModel);
	protected final SymbolOverlapFinder<BoundedSymbol> overlapFinder=new SymbolOverlapFinder<BoundedSymbol>(overlapModel);
	protected final LabelCandidatePriorityComparator costComparator=new LabelCandidatePriorityComparator(costModel);
	protected final PriorityQueue<LabelCandidate> candidateQueue=new PriorityQueue<LabelCandidate>(1,costComparator);
	
	public void addLabeledObject(LabeledObject object) {
		Set<LabelCandidate> candidates=object.getLabelCandidates();
		
		labeledObjects.add(object);
		labelCandidates.addAll(candidates);
		
		overlapFinder.addSymbol(object);
		overlapFinder.addSymbols(candidates);
		
		costModel.registerLabeledObject(object);
	}
	
	public void addProtectedSymbol(ProtectedSymbol symbol) {
		overlapFinder.addSymbol(symbol);
	}
	
	public void prepare() {
		overlapFinder.run();
		costModel.prepare();
		
		candidateQueue.addAll(labelCandidates);
	}
	
	public void removeOne() {
		if (costModel.getMultiCandidateObjectCount()==0 || candidateQueue.isEmpty())
			return;
		LabelCandidate candidate=candidateQueue.remove();
		Set<LabelCandidate> updatedCandidates=new HashSet<LabelCandidate>();
		
		costModel.removeCandidate(candidate, updatedCandidates);
		candidateQueue.removeAll(updatedCandidates);
		costModel.updateCandidateCosts(updatedCandidates);
		candidateQueue.addAll(updatedCandidates);
	}
	
	public void label() {
		while (costModel.getMultiCandidateObjectCount()!=0 && !candidateQueue.isEmpty()) {
			LabelCandidate candidate=candidateQueue.remove();
			Set<LabelCandidate> updatedCandidates=new HashSet<LabelCandidate>();
			
			costModel.removeCandidate(candidate, updatedCandidates);
			candidateQueue.removeAll(updatedCandidates);
			costModel.updateCandidateCosts(updatedCandidates);
			candidateQueue.addAll(updatedCandidates);
		}
	}
	
	public LabelCandidate getNextCandidate() {
		return candidateQueue.peek();
	}
	
	public LabelCostModel getCostModel() {
		return costModel;
	}
	
	public OverlapModel<BoundedSymbol> getOverlapModel() {
		return overlapModel;
	}
	
	public Collection<LabelCandidate> getCandidates() {
		return candidateQueue;
	}
}
