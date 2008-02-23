package de.knewcleus.radar.autolabel;

public interface LabelCandidate extends BoundedSymbol {
	public LabeledObject getAssociatedObject();
	public double getCost();
}
