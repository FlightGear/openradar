package de.knewcleus.radar.ui.labels;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface ILabelElement {
	/**
	 * Calculate the internal layout parameters.
	 */
	public void layout();
	
	/**
	 * Get the minimum size of this label element required.
	 */
	public Dimension getMinimumSize();
	
	/**
	 * Get the distance from the baseline to the top of the element.
	 * @return
	 */
	public int getAscent();
	
	/**
	 * Get the assigned bounds of this label element.
	 */
	public Rectangle getBounds();
	
	/**
	 * Set the bounds of this label element.
	 */
	public void setBounds(Rectangle rectangle);
	
	/**
	 * Paint the label element using the given graphics context.
	 * 
	 * @param g2d
	 */
	public void paint(Graphics2D g2d);
}
