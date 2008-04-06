package de.knewcleus.radar.autolabel;

public interface Label extends ChargedSymbol {
	public LabeledObject getAssociatedObject();
	public void setCentroidPosition(double x, double y);
}
