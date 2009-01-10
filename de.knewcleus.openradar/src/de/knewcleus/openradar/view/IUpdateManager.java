package de.knewcleus.openradar.view;

import java.awt.geom.Rectangle2D;

/**
 * A viewer repaint manager schedules dirty regions for repaint.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IUpdateManager {
	/**
	 * Mark the whole viewport as dirty, scheduling a total repaint.
	 */
	public void markViewportDirty();
	
	/**
	 * Mark the given region as dirty, scheduling a repaint of the region.
	 * 
	 * @param bounds	The bounds of the region to be redrawn.
	 */
	public void markRegionDirty(Rectangle2D bounds);
	
	/**
	 * Mark the given view as invalid, scheduling a revalidation.
	 */
	public void markViewInvalid(IView view);

	public abstract void setRootView(IView rootView);

	public abstract IView getRootView();
}
