package de.knewcleus.openradar.map.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.map.CoordinateSystemNotification;
import de.knewcleus.openradar.map.IBoundedView;
import de.knewcleus.openradar.map.IMapViewAdapter;
import de.knewcleus.openradar.map.IPickable;
import de.knewcleus.openradar.map.IViewVisitor;
import de.knewcleus.openradar.map.ViewNotification;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.notify.Notifier;

public class PickableView extends Notifier implements IPickable, IBoundedView, INotificationListener {
	protected final IMapViewAdapter mapViewAdapter;
	protected final Rectangle2D logicalBounds;
	protected final Color selectedColor;
	protected final Color unselectedColor;
	protected boolean selected = false;
	
	protected Rectangle2D deviceBounds = null;

	public PickableView(IMapViewAdapter mapViewAdapter,
			Rectangle2D logicalBounds,
			Color selectedColor, Color unselectedColor) {
		this.mapViewAdapter = mapViewAdapter;
		this.logicalBounds = logicalBounds;
		this.selectedColor = selectedColor;
		this.unselectedColor = unselectedColor;
		
		mapViewAdapter.registerListener(this);
	}
	
	public void setSelected(boolean selected) {
		if (selected == this.selected) {
			return;
		}
		this.selected = selected;
		fireViewNotification(new ViewNotification(this));
	}

	protected void fireViewNotification(ViewNotification viewNotification) {
		notify(viewNotification);
		mapViewAdapter.acceptNotification(viewNotification);
	}

	@Override
	public boolean contains(Point2D devicePoint) {
		final AffineTransform deviceToLogical = mapViewAdapter.getDeviceToLogicalTransform();
		final Point2D logicalPoint = deviceToLogical.transform(devicePoint, null);
		return logicalBounds.contains(logicalPoint);
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		if (deviceBounds != null) {
			return deviceBounds;
		}
		final AffineTransform logicalToDevice = mapViewAdapter.getLogicalToDeviceTransform();
		final Shape deviceShape = logicalToDevice.createTransformedShape(logicalBounds);
		deviceBounds = deviceShape.getBounds2D();
		return deviceBounds;
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}

	@Override
	public void paint(Graphics2D g2d) {
		final AffineTransform oldTransform = g2d.getTransform();
		final AffineTransform logicalToDevice = mapViewAdapter.getLogicalToDeviceTransform();
		g2d.transform(logicalToDevice);
		g2d.setColor(selected?selectedColor:unselectedColor);
		g2d.fill(logicalBounds);
		g2d.setTransform(oldTransform);
		g2d.setColor(Color.GRAY);
		g2d.draw(getDisplayExtents());
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			invalidateDeviceBounds();
		}
	}

	protected void invalidateDeviceBounds() {
		deviceBounds = null;
		fireViewNotification(new ViewNotification(this));
	}
}
