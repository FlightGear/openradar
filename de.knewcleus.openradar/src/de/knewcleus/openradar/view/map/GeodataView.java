/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.map;

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
import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotificationListener;
import de.knewcleus.openradar.view.CoordinateSystemNotification;
import de.knewcleus.openradar.view.IBoundedView;
import de.knewcleus.openradar.view.IViewVisitor;

public class GeodataView implements IBoundedView, INotificationListener {
	protected final IMapViewerAdapter mapViewAdapter;
	protected final List<Geometry> geometries = new ArrayList<Geometry>();
	
	protected Color color = Color.BLACK;
	protected boolean fill = true;
	protected boolean visible = true;
	
	protected Rectangle2D displayExtents = null;
	protected Rectangle2D logicalBounds = null;
	protected List<Shape> logicalShapes = null;

	public GeodataView(IMapViewerAdapter mapViewAdapter, IGeodataLayer geodataLayer) throws GeodataException {
		this.mapViewAdapter = mapViewAdapter;
		mapViewAdapter.registerListener(this);
		Feature feature;
		while ((feature=geodataLayer.getNextFeature())!=null) {
			geometries.add(feature.getGeometry());
		}
		updateLogicalShapes();
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
		repaint();
	}
	
	public boolean isFill() {
		return fill;
	}
	
	public void setFill(boolean fill) {
		this.fill = fill;
		repaint();
	}

	@Override
	public void accept(IViewVisitor visitor) {
		visitor.visitView(this);
	}
	
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisible(boolean visible) {
		if (this.visible==visible) {
			return;
		}
		this.visible = visible;
		mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}
	
	@Override
	public void acceptNotification(INotification notification) {
		if (notification instanceof ProjectionNotification) {
			updateLogicalShapes();
		} else if (notification instanceof CoordinateSystemNotification) {
			updateDisplayExtents();
		}
	}
	
	@Override
	public Rectangle2D getDisplayExtents() {
		return displayExtents;
	}
	
	@Override
	public void paint(Graphics2D g2d) {
		g2d.setColor(color);
		final AffineTransform oldTransform = g2d.getTransform();
		final AffineTransform logicalToDevice = mapViewAdapter.getLogicalToDeviceTransform();
		g2d.transform(logicalToDevice);
		final List<Shape> shapes = logicalShapes;
		if (isFill()) {
			for (Shape shape: shapes) {
				g2d.fill(shape);
			}
		} else {
			for (Shape shape: shapes) {
				g2d.draw(shape);
			}
		}
		g2d.setTransform(oldTransform);
	}
	
	protected void updateLogicalShapes() {
		final IProjection projection = mapViewAdapter.getProjection();
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
		updateLogicalBounds();
	}
	
	protected void updateLogicalBounds() {
		logicalBounds = new Rectangle2D.Double();
		for (Shape shape: logicalShapes) {
			final Rectangle2D bounds = shape.getBounds2D();
			Rectangle2D.union(logicalBounds, bounds, logicalBounds);
		}
		updateDisplayExtents();
	}
	
	protected void updateDisplayExtents() {
		if (displayExtents!=null) {
			/* Make sure the previously occupied region is repainted */
			mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
		}
		final AffineTransform logicalToDevice = mapViewAdapter.getLogicalToDeviceTransform();
		displayExtents = logicalToDevice.createTransformedShape(logicalBounds).getBounds2D();
		repaint();
	}
	
	@Override
	public void validate() {
		logicalBounds = null;
		logicalShapes = null;
		displayExtents = null;
		repaint();
	}
	
	protected void repaint() {
		mapViewAdapter.getUpdateManager().markRegionDirty(displayExtents);
	}
}
