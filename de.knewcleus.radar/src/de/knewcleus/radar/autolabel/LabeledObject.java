package de.knewcleus.radar.autolabel;

import java.awt.geom.Point2D;

public interface LabeledObject extends ChargedSymbol {
	public Label getLabel();
	public PotentialGradient getPotentialGradient(Point2D pos);
	public boolean isLocked();
}
