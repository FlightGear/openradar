package de.knewcleus.openradar.map;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.notify.INotifier;

/**
 * A view adapter centrally manages the aspects of a viewer,
 * such as the logical and device coordinate systems.
 * 
 * It is also the central interface between the viewer and the views.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IViewAdapter extends INotifier, INotificationListener {

	/**
	 * @return the current device extents of the viewer.
	 */
	public abstract Rectangle2D getViewerExtents();

	/**
	 * Set the device extents of the viewer and issue a notification about the change.
	 */
	public abstract void setViewerExtents(Rectangle2D extents);

	/**
	 * @return the current logical-to-device transformation for this map.
	 */
	public abstract AffineTransform getLogicalToDeviceTransform();

	/**
	 * @return the current device-to-logical transformation for this map.
	 */
	public abstract AffineTransform getDeviceToLogicalTransform();

	/**
	 * @return the current logical scale.
	 */
	public abstract double getLogicalScale();

	/**
	 * Set the logical scale and issue a notification about the change.
	 * @param scale		The new scale.
	 */
	public abstract void setLogicalScale(double scale);

	/**
	 * @return the current logical x-offset.
	 */
	public abstract double getLogicalOffsetX();

	/**
	 * @return the current logical y-offset.
	 */
	public abstract double getLogicalOffsetY();

	/**
	 * Set the logical offsets and issue a notification about the change.
	 * @param offsetX	The new logical x-offset
	 * @param offsetY	The new logical y-offset
	 */
	public abstract void setLogicalOffset(double offsetX, double offsetY);

}