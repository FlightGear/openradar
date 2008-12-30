package de.knewcleus.openradar.map;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.Notifier;

public class MapViewAdapter extends Notifier implements IMapViewAdapter {
	protected double logicalScale=1.0;
	protected double logicalOffsetX=0.0;
	protected double logicalOffsetY=0.0;
	protected Rectangle2D viewerExtents = new Rectangle2D.Double();
	protected AffineTransform deviceToLogicalTransform = null;
	protected AffineTransform logicalToDeviceTransform = null;
	protected IProjection projection = new IdentityProjection();
	
	@Override
	public Rectangle2D getViewerExtents() {
		return viewerExtents;
	}
	
	@Override
	public void setViewerExtents(Rectangle2D extents) {
		viewerExtents = extents;
		invalidateTransforms();
	}
	
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
		final double cx = viewerExtents.getCenterX();
		final double cy = viewerExtents.getCenterY();
		
		deviceToLogicalTransform = new AffineTransform(
				logicalScale, 0,
				0, -logicalScale,
				- logicalOffsetX - cx * logicalScale,
				- logicalOffsetY + cy * logicalScale);
		logicalToDeviceTransform = new AffineTransform(
				1.0/logicalScale, 0,
				0, -1.0/logicalScale,
				logicalOffsetX/logicalScale + cx,
				- logicalOffsetY/logicalScale + cy);
	}

	@Override
	public void acceptNotification(INotification notification) {
		/* Simply forward the notifications */
		notify(notification);
	}
}
