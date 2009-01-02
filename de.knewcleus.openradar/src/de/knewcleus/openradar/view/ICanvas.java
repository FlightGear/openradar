package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface ICanvas {
	public Graphics2D getGraphics(Rectangle2D region);
}
