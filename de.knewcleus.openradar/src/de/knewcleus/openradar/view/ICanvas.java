package de.knewcleus.openradar.view;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface ICanvas {
	/**
	 * Return a Java2D graphics context for drawing into the given region on the canvas.
	 * 
	 * @param region	The region of the canvas to draw to.
	 * @return the graphics context.
	 */
	public Graphics2D getGraphics(Rectangle2D region);
	
	/**
	 * Flush graphics to the canvas.
	 */
	public void flushGraphics();
}
