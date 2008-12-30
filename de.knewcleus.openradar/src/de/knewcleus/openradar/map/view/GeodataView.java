package de.knewcleus.openradar.map.view;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.geodata.Feature;
import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.IGeodataLayer;
import de.knewcleus.fgfs.geodata.geometry.Geometry;
import de.knewcleus.openradar.map.CoordinateSystemNotification;
import de.knewcleus.openradar.map.IBoundedView;
import de.knewcleus.openradar.map.ILayer;
import de.knewcleus.openradar.map.IMap;
import de.knewcleus.openradar.map.IProjection;
import de.knewcleus.openradar.map.IViewVisitor;
import de.knewcleus.openradar.map.ViewNotification;
import de.knewcleus.openradar.map.util.GeometryToShapeProjector;
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.notify.Notifier;

public class GeodataView extends Notifier implements ILayer, IBoundedView, INotificationListener {
	protected final IMap map;
	protected final String name;
	protected final List<Geometry> geometries = new ArrayList<Geometry>();
	
	protected Color color = Color.BLACK;
	protected boolean fill = true;
	
	protected Rectangle2D deviceBounds = null;
	protected List<Shape> deviceShapes = null;
	protected List<Shape> logicalShapes = null;

	public GeodataView(IMap map, String name, IGeodataLayer geodataLayer) throws GeodataException {
		this.map = map;
		this.name = name;
		map.registerListener(this);
		Feature feature;
		while ((feature=geodataLayer.getNextFeature())!=null) {
			geometries.add(feature.getGeometry());
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		notify(new ViewNotification(this));
	}
	
	public boolean isFill() {
		return fill;
	}
	
	public void setFill(boolean fill) {
		this.fill = fill;
		notify(new ViewNotification(this));
	}

	@Override
	public IMap getMap() {
		return map;
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof CoordinateSystemNotification) {
			final CoordinateSystemNotification coordinateSystemNotification;
			coordinateSystemNotification=(CoordinateSystemNotification)notification;
			if (coordinateSystemNotification.isProjectionChanged()) {
				invalidateLogicalShapes();
			}
			if (coordinateSystemNotification.isTransformationChanged()) {
				invalidateDeviceShapes();
			}
			// TODO: shouldn't we send an individual view notification here?
		}
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		if (deviceBounds != null) {
			return deviceBounds;
		}
		final List<Shape> deviceShapes = getDeviceShapes();
		deviceBounds = new Rectangle2D.Double();
		for (Shape deviceShape: deviceShapes) {
			final Rectangle2D bounds = deviceShape.getBounds2D();
			Rectangle2D.union(deviceBounds, bounds, deviceBounds);
		}
		return deviceBounds;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(color);
		final List<Shape> deviceShapes = getDeviceShapes();
		if (isFill()) {
			for (Shape deviceShape: deviceShapes) {
				g2d.fill(deviceShape);
			}
		} else {
			for (Shape deviceShape: deviceShapes) {
				g2d.draw(deviceShape);
			}
		}
	}

	protected List<Shape> getDeviceShapes() {
		if (deviceShapes != null) {
			return deviceShapes;
		}
		final AffineTransform logicalToDevice;
		logicalToDevice = getMap().getLogicalToDeviceTransform();
		final List<Shape> logicalShapes = getLogicalShapes();
		deviceShapes = new ArrayList<Shape>();
		for (Shape logicalShape: logicalShapes) {
			deviceShapes.add(logicalToDevice.createTransformedShape(logicalShape));
		}
		return deviceShapes;
	}

	protected void invalidateDeviceShapes() {
		deviceShapes = null;
		deviceBounds = null;
	}
	
	protected List<Shape> getLogicalShapes() {
		if (logicalShapes != null) {
			return logicalShapes;
		}
		final IProjection projection = getMap().getProjection();
		final GeometryToShapeProjector shapeProjector;
		shapeProjector = new GeometryToShapeProjector(projection);
		for (Geometry geometry: geometries) {
			try {
				geometry.accept(shapeProjector);
			} catch (GeodataException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		logicalShapes = shapeProjector.getShapes();
		return logicalShapes;
	}

	protected void invalidateLogicalShapes() {
		logicalShapes = null;
		invalidateDeviceShapes();
	}
}
