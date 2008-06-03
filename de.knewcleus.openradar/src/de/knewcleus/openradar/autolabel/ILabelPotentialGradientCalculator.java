package de.knewcleus.openradar.autolabel;

import java.awt.geom.Point2D;

public interface ILabelPotentialGradientCalculator {
	public PotentialGradient getPotentialGradient(ILabel label, Point2D pos);
}
