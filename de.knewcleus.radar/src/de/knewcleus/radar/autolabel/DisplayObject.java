package de.knewcleus.radar.autolabel;

import java.awt.geom.Rectangle2D;

public interface DisplayObject {
	public Rectangle2D getBounds2D();
	public double getPriority();
}
