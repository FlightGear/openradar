package de.knewcleus.openradar.map;

import java.awt.geom.AffineTransform;

import de.knewcleus.openradar.notify.INotifier;

public interface IMap extends INotifier, IContainer {
	/**
	 * @return the current projection for this map.
	 */
	public IProjection getProjection();
	
	/**
	 * @return the current logical-to-device transformation for this map.
	 */
	public AffineTransform getLogicalToDeviceTransform();
	
	/**
	 * @return the current device-to-logical transformation for this map.
	 */
	public AffineTransform getDeviceToLogicalTransform();

	public double getLogicalScale();

	public void setLogicalScale(double scale);
	
	public double getLogicalOffsetX();
	public double getLogicalOffsetY();
	
	public void setLogicalOffset(double offsetX, double offsetY);
}
