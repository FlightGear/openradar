package de.knewcleus.radar.autolabel;

public interface Label extends ChargedSymbol {
	public LabeledObject getAssociatedObject();
	public void move(double dx, double dy);
	public double getHookX();
	public double getHookY();
}
