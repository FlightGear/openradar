package de.knewcleus.radar.autolabel;

import java.awt.geom.Rectangle2D;

public interface ChargedSymbol {
	public Rectangle2D getBounds2D();
	public double getChargeDensity();
}
