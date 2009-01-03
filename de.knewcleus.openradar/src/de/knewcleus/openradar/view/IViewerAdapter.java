package de.knewcleus.openradar.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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
public interface IViewerAdapter extends INotifier {
	/**
	 * Revalidate the adapter.
	 * 
	 * @see IView#revalidate()
	 */
	public abstract void revalidate();

	/**
	 * @return the current device extents of the viewer.
	 */
	public abstract Rectangle2D getViewerExtents();

	/**
	 * Set the device extents of the viewer and issue a notification about the change.
	 */
	public abstract void setViewerExtents(Rectangle2D extents);
	
	/**
	 * @return the update manager for the viewer.
	 */
	public abstract IUpdateManager getUpdateManager();
	
	/**
	 * @return the root view.
	 */
	public abstract IView getRootView();

	/**
	 * @return the current logical-to-device transformation for this map.
	 */
	public abstract AffineTransform getLogicalToDeviceTransform();

	/**
	 * @return the current device-to-logical transformation for this map.
	 */
	public abstract AffineTransform getDeviceToLogicalTransform();
	
	/**
	 * @return the current device x-origin
	 */
	public abstract double getDeviceOriginX();
	
	/**
	 * @return the current device y-origin
	 */
	public abstract double getDeviceOriginY();
	
	/**
	 * Set the device origin and issue a notification about the change.
	 * 
	 * @param originX	The new x-origin
	 * @param originY	The new y-origin
	 */
	public abstract void setDeviceOrigin(double originX, double originY);

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
	 * @return the current logical x-origin.
	 */
	public abstract double getLogicalOriginX();

	/**
	 * @return the current logical y-origin.
	 */
	public abstract double getLogicalOriginY();

	/**
	 * Set the logical origin and issue a notification about the change.
	 * @param originX	The new logical x-origin
	 * @param originY	The new logical y-origin
	 */
	public abstract void setLogicalOrigin(double originX, double originY);

}