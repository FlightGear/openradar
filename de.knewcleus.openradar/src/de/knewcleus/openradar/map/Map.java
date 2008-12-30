package de.knewcleus.openradar.map;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.notify.Notifier;

public class Map extends Notifier implements IMap {
	protected double scale=1.0;
	protected double offsetX=0.0;
	protected double offsetY=0.0;
	protected AffineTransform deviceToLogicalTransform = null;
	protected AffineTransform logicalToDeviceTransform = null;
	protected IProjection projection = null;
	protected final List<ILayer> layers=new ArrayList<ILayer>();
	
	@Override
	public IProjection getProjection() {
		throw new UnsupportedOperationException();
	}

	@Override
	public IMap getMap() {
		return this;
	}
	
	public void pushLayer(ILayer layer) {
		layers.add(layer);
		notifyStructuralChange(layer, StructuralNotification.ChangeType.ADD);
	}
	
	public void removeLayer(ILayer layer) {
		layers.remove(layer);
		notifyStructuralChange(layer, StructuralNotification.ChangeType.REMOVE);
	}
	
	protected void notifyStructuralChange(ILayer layer, StructuralNotification.ChangeType changeType) {
		notify(new StructuralNotification(this, layer, changeType));
		notify(new ViewNotification(this));
	}
	
	@Override
	public double getScale() {
		return scale;
	}
	
	@Override
	public void setScale(double scale) {
		this.scale = scale;
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
		deviceToLogicalTransform = null;
		logicalToDeviceTransform = null;
		notify(new CoordinateSystemNotification(this));
		notify(new ViewNotification(this));
	}
	
	protected void updateTransforms() {
		deviceToLogicalTransform = new AffineTransform(
				scale, 0,
				0, scale,
				offsetX, offsetY);
		logicalToDeviceTransform = new AffineTransform(
				1.0/scale, 0,
				0, 1.0/scale,
				-offsetX/scale, -offsetY/scale);
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
