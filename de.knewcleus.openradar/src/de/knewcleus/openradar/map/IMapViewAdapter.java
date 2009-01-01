package de.knewcleus.openradar.map;

/**
 * A map view adapter is a view adapter for a map viewer.

 * @author Ralf Gerlich
 *
 */
public interface IMapViewAdapter extends IViewAdapter {
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
