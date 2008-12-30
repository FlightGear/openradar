package de.knewcleus.openradar.map;

import java.awt.Graphics2D;

import de.knewcleus.openradar.notify.INotifier;

/**
 * A view is an entity on the map display.
 * 
 * @author Ralf Gerlich
 */
public interface IView extends INotifier {
	/**
	 * Paint this view.
	 * @param g2d  The graphics context to paint on.
	 */
	public void paint(Graphics2D g2d);
	
	/**
	 * Accept the view visitor.
	 */
	public void accept(IViewVisitor visitor);
}
