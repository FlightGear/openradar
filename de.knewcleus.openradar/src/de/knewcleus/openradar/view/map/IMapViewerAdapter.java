package de.knewcleus.openradar.view.map;

import de.knewcleus.openradar.view.IViewerAdapter;

/**
 * A map viewer adapter is a viewer adapter for a map viewer.

 * @author Ralf Gerlich
 *
 */
public interface IMapViewerAdapter extends IViewerAdapter {
	/**
	 * @return the current projection for this map.
	 */
	public IProjection getProjection();
	
	/**
	 * Set the projection and issue a notification about the change.
	 * @param projection	The new projection
	 */
	public void setProjection(IProjection projection);
}
