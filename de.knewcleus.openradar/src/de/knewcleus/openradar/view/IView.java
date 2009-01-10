package de.knewcleus.openradar.view;

import java.awt.Graphics2D;

/**
 * A view is an entity on the map display.
 * 
 * @author Ralf Gerlich
 */
public interface IView {
	/**
	 * Paint this view.
	 * @param g2d  The graphics context to paint on.
	 */
	public void paint(Graphics2D g2d);
	
	/**
	 * Revalidate this view.
	 * 
	 * Here views should recalculate device coordinates, layout and similar.
	 */
	public void validate();
	
	/**
	 * Accept the view visitor.
	 */
	public void accept(IViewVisitor visitor);
}
