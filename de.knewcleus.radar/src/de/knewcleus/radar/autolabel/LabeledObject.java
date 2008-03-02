package de.knewcleus.radar.autolabel;

public interface LabeledObject extends ChargedSymbol {
	public Label getLabel();
	public PotentialGradient getPotentialGradient(double x, double y);
	public boolean isLocked();
}
