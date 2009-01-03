package de.knewcleus.openradar.view;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.notify.Notifier;

public class ViewerAdapter extends Notifier implements IViewerAdapter {
	protected Rectangle2D viewerExtents = new Rectangle2D.Double();
	protected IUpdateManager updateManager = new DeferredUpdateManager(this); 
	protected final LayeredView rootView = new LayeredView(this);
	protected double logicalScale = 1.0;
	protected double logicalOriginX = 0.0;
	protected double logicalOriginY = 0.0;
	protected double deviceOriginX = 0.0;
	protected double deviceOriginY = 0.0;
	protected AffineTransform deviceToLogicalTransform = null;
	protected AffineTransform logicalToDeviceTransform = null;

	public ViewerAdapter() {
		super();
	}

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
	public IUpdateManager getUpdateManager() {
		return updateManager;
	}
	
	@Override
	public LayeredView getRootView() {
		return rootView;
	}
	
	@Override
	public double getDeviceOriginX() {
		return deviceOriginX;
	}
	
	@Override
	public double getDeviceOriginY() {
		return deviceOriginY;
	}
	
	@Override
	public void setDeviceOrigin(double originX, double originY) {
		deviceOriginX = originX;
		deviceOriginY = originY;
		invalidateTransforms();
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
	public double getLogicalOriginX() {
		return logicalOriginX;
	}

	@Override
	public double getLogicalOriginY() {
		return logicalOriginY;
	}

	@Override
	public void setLogicalOrigin(double offsetX, double offsetY) {
		this.logicalOriginX = offsetX;
		this.logicalOriginY = offsetY;
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
		notify(new CoordinateSystemNotification(this));
	}

	@Override
	public void revalidate() {
		deviceToLogicalTransform = null;
		logicalToDeviceTransform = null;
	}
	
	protected void updateTransforms() {
		deviceToLogicalTransform = new AffineTransform(
				logicalScale, 0,
				0, -logicalScale,
				logicalOriginX - logicalScale * deviceOriginX,
				logicalOriginY + logicalScale * deviceOriginY);
		logicalToDeviceTransform = new AffineTransform(
				1.0/logicalScale, 0,
				0, -1.0/logicalScale,
				deviceOriginX - logicalOriginX/logicalScale,
				deviceOriginY + logicalOriginY/logicalScale);
	}
}