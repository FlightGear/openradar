package de.knewcleus.openradar.map;

import java.awt.geom.Rectangle2D;

/**
 * A bounded view is a view with finite extents.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IBoundedView extends IView {
	/**
	 * Determine the extents of the view on the display.
	 * 
	 * The returned object may not be modified.
	 * 
	 * @return the extents of the view on the display.
	 */
	public Rectangle2D getDisplayExtents();
}
