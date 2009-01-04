package de.knewcleus.openradar.view.layout;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

/**
 * A layout manager represents a layout algorithm for a layout part container.
 * 
 * @author Ralf Gerlich
 * @see ILayoutPartContainer
 * @see ILayoutPart
 *
 */
public interface ILayoutManager {
	/**
	 * @return the minimum size required to layout the elements in the associated container.
	 */
	public Dimension2D getMinimumSize();
	
	/**
	 * @return the preferred size required to layout the elements in the associated container.
	 */
	public Dimension2D getPreferredSize();
	
	/**
	 * Mark the layout as invalid and ensure it is recalculated.
	 */
	public void invalidate();
	
	/**
	 * Layout the associated container in the given bounds.
	 */
	public void layout(Rectangle2D bounds);
}
