package de.knewcleus.openradar.map;

import java.awt.geom.AffineTransform;

import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.notify.INotifier;

/**
 * A map view adapter centrally manages the aspects of a map view,
 * such as the projected, logical and device coordinate systems.
 * 
 * It is also the central interface between the map viewer and the map elements.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IMapViewAdapter extends INotifier, INotificationListener {
	/**
	 * @return the current projection for this map.
	 */
	public IProjection getProjection();
	
	/**
	 * Set the projection and issue a notification about the change.
	 * @param projection	The new projection
	 */
	public void setProjection(IProjection projection);
	
	/**
	 * @return the current logical-to-device transformation for this map.
	 */
	public AffineTransform getLogicalToDeviceTransform();
	
	/**
	 * @return the current device-to-logical transformation for this map.
	 */
	public AffineTransform getDeviceToLogicalTransform();

	/**
	 * @return the current logical scale.
	 */
	public double getLogicalScale();

	/**
	 * Set the logical scale and issue a notification about the change.
	 * @param scale		The new scale.
	 */
	public void setLogicalScale(double scale);
	
	/**
	 * @return the current logical x-offset.
	 */
	public double getLogicalOffsetX();

	/**
	 * @return the current logical y-offset.
	 */
	public double getLogicalOffsetY();
	
	/**
	 * Set the logical offsets and issue a notification about the change.
	 * @param offsetX	The new logical x-offset
	 * @param offsetY	The new logical y-offset
	 */
	public void setLogicalOffset(double offsetX, double offsetY);
}
