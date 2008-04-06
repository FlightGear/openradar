package de.knewcleus.radar.autolabel;

import java.awt.geom.Point2D;

public interface ILabelPotentialGradientCalculator {
	public PotentialGradient getPotentialGradient(Label label, Point2D pos);
}
