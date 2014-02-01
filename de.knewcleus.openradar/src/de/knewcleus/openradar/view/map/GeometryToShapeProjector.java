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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.geodata.GeodataException;
import de.knewcleus.fgfs.geodata.InvalidGeometryException;
import de.knewcleus.fgfs.geodata.UnsupportedGeometryException;
import de.knewcleus.fgfs.geodata.geometry.Geometry;
import de.knewcleus.fgfs.geodata.geometry.GeometryContainer;
import de.knewcleus.fgfs.geodata.geometry.IGeometryVisitor;
import de.knewcleus.fgfs.geodata.geometry.LineString;
import de.knewcleus.fgfs.geodata.geometry.MultiLineString;
import de.knewcleus.fgfs.geodata.geometry.NullShape;
import de.knewcleus.fgfs.geodata.geometry.Point;
import de.knewcleus.fgfs.geodata.geometry.Polygon;
import de.knewcleus.fgfs.geodata.geometry.Ring;
import de.knewcleus.openradar.gui.setup.AirportData;

public class GeometryToShapeProjector implements IGeometryVisitor {
	protected final IProjection projection;
	protected final AirportData data;
	public final static String TOGGLE_STATE="projection.nice";
	protected final List<Shape> shapes=new ArrayList<Shape>();
	
	public GeometryToShapeProjector(AirportData data, IProjection projection) {
		this.projection = projection;
		this.data = data;
	}
	
	public List<Shape> getShapes() {
		return shapes;
	}
	
	@Override
	public void visit(Polygon polygon) throws GeodataException {
		final GeometryToShapeProjector ringProjector;
		ringProjector = new GeometryToShapeProjector(data, projection);
		polygon.traverse(ringProjector);
		
		final Iterator<Shape> shapeIterator = ringProjector.getShapes().iterator();
		
		if (!shapeIterator.hasNext()) {
			throw new InvalidGeometryException("Empty polygon encountered");
		}
		
		final Shape outerShape = shapeIterator.next();
		
		if(data.getToggleState(TOGGLE_STATE, true)) {
		    // performance issue: This loop is looping over all existing shapes => gets slower with growing shape count
    		final Area polygonArea = new Area(outerShape);
    
    		while (shapeIterator.hasNext()) {
    			final Shape innerShape = shapeIterator.next();
    			polygonArea.subtract(new Area(innerShape));
    		}
    		
    		shapes.add(polygonArea);
		} else {		
		    // this quick solution does not display islands in a layer
		    shapes.add(outerShape);
		}
	}
	
	@Override
	public void visit(Ring ring) throws GeodataException {
		Path2D path = convertLinestring(ring);
		path.closePath();
		
		shapes.add(path);
	}
	
	@Override
	public void visit(LineString linestring) throws GeodataException {
		shapes.add(convertLinestring(linestring));
	}
	
	@Override
	public void visit(MultiLineString multilinestring) throws GeodataException {
		multilinestring.traverse(this);
	}
	
	@Override
	public void visit(GeometryContainer<?> container) throws GeodataException {
		container.traverse(this);
	}
	
	@Override
	public void visit(Point point) throws GeodataException {
		throw new UnsupportedGeometryException("Cannot convert a Point geometry");
	}
	
	@Override
	public void visit(Geometry geometry) throws GeodataException {
		throw new UnsupportedGeometryException("Unknown geometry type "+geometry.getClass());
	}
	
	@Override
	public void visit(NullShape nullshape) throws GeodataException {
		/* Nothing to do here */
	}
	
	protected Path2D convertLinestring(LineString linestring) throws GeodataException {
		Path2D path = new Path2D.Double();
		
		Iterator<Point> posIterator=linestring.iterator();
		
		if (!posIterator.hasNext()) {
			throw new InvalidGeometryException("Empty linestring encountered");
		}
		
		Point point=posIterator.next();
		Point2D projectedPoint = project(point);
		
		path.moveTo(projectedPoint.getX(), projectedPoint.getY());
		
		while (posIterator.hasNext()) {
			point=posIterator.next();
			projectedPoint = project(point);
			path.lineTo(projectedPoint.getX(), projectedPoint.getY());
		}
		
		return path;
	}
	
	protected Point2D project(Point point) {
		Point2D in = new Point2D.Double(point.getX(), point.getY());
		return projection.toLogical(in);
	}
}
