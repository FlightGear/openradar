package de.knewcleus.openradar.map;

import java.awt.geom.AffineTransform;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.Notifier;

public class MapViewAdapter extends Notifier implements IMapViewAdapter {
	protected double logicalScale=1.0;
	protected double logicalOffsetX=0.0;
	protected double logicalOffsetY=0.0;
	protected AffineTransform deviceToLogicalTransform = null;
	protected AffineTransform logicalToDeviceTransform = null;
	protected IProjection projection = new IdentityProjection();
	
	@Override
	public IProjection getProjection() {
		return projection;
	}
	
	public void setProjection(IProjection projection) {
		this.projection = projection;
		notify(new CoordinateSystemNotification(this, false, true));
	}
	
	@Override
	public double getLogicalScale() {
		return logicalScale;
	}
	
	@Override
	public void setLogicalScale(double scale) {
		this.logicalScale = scale;
		invalidateTransforms();
	}
	
	@Override
	public double getLogicalOffsetX() {
		return logicalOffsetX;
	}
	
	@Override
	public double getLogicalOffsetY() {
		return logicalOffsetY;
	}
	
	@Override
	public void setLogicalOffset(double offsetX, double offsetY) {
		this.logicalOffsetX = offsetX;
		this.logicalOffsetY = offsetY;
		invalidateTransforms();
	}
	
	@Override
	public AffineTransform getDeviceToLogicalTransform() {
		if (deviceToLogicalTransform==null) {
			updateTransforms();
		}
		return deviceToLogicalTransform;
	}
	
	@Override
	public AffineTransform getLogicalToDeviceTransform() {
		if (logicalToDeviceTransform==null) {
			updateTransforms();
		}
		return logicalToDeviceTransform;
	}
	
	protected void invalidateTransforms() {
		if (deviceToLogicalTransform==null || logicalToDeviceTransform==null) {
			/* No need to invalidate and update if they are still invalidated */
			return;
		}
		deviceToLogicalTransform = null;
		logicalToDeviceTransform = null;
		notify(new CoordinateSystemNotification(this, true, false));
	}
	
	protected void updateTransforms() {
		deviceToLogicalTransform = new AffineTransform(
				logicalScale, 0,
				0, -logicalScale,
				logicalOffsetX, -logicalOffsetY);
		logicalToDeviceTransform = new AffineTransform(
				1.0/logicalScale, 0,
				0, -1.0/logicalScale,
				-logicalOffsetX/logicalScale, logicalOffsetY/logicalScale);
	}

	@Override
	public void acceptNotification(INotification notification) {
		/* Simply forward the notifications */
		notify(notification);
	}
}
