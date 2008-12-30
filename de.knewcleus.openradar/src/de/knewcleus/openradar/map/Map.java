package de.knewcleus.openradar.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.notify.Notifier;

public class Map extends Notifier implements IMap {
	protected double logicalScale=1.0;
	protected double logicalOffsetX=0.0;
	protected double logicalOffsetY=0.0;
	protected double deviceOffsetX=0.0;
	protected double deviceOffsetY=0.0;
	protected AffineTransform deviceToLogicalTransform = null;
	protected AffineTransform logicalToDeviceTransform = null;
	protected IProjection projection = new IdentityProjection();
	protected final List<ILayer> layers=new ArrayList<ILayer>();
	
	@Override
	public IProjection getProjection() {
		return projection;
	}
	
	public void setProjection(IProjection projection) {
		this.projection = projection;
		notify(new CoordinateSystemNotification(this, false, true));
	}

	@Override
	public IMap getMap() {
		return this;
	}
	
	public void pushLayer(ILayer layer) {
		layers.add(layer);
		notify(new StructuralNotification(this,
				layer,
				StructuralNotification.ChangeType.ADD));
	}
	
	public void removeLayer(ILayer layer) {
		layers.remove(layer);
		notify(new StructuralNotification(this,
				layer,
				StructuralNotification.ChangeType.REMOVE));
	}
	
	protected void notifyStructuralChange(ILayer layer, StructuralNotification.ChangeType changeType) {
		notify(new StructuralNotification(this, layer, changeType));
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
	public void accept(IViewVisitor visitor) {
		visitor.visitContainer(this);
	}
	
	@Override
	public void traverse(IViewVisitor visitor) {
		for (ILayer layer: layers) {
			layer.accept(visitor);
		}
	}

	@Override
	public void paint(Graphics2D g2d) {}
}
