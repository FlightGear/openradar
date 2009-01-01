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

public class GeometryToShapeProjector implements IGeometryVisitor {
	protected final IProjection projection;
	protected final List<Shape> shapes=new ArrayList<Shape>();
	
	public GeometryToShapeProjector(IProjection projection) {
		this.projection = projection;
	}
	
	public List<Shape> getShapes() {
		return shapes;
	}
	
	@Override
	public void visit(Polygon polygon) throws GeodataException {
		final GeometryToShapeProjector ringProjector;
		ringProjector = new GeometryToShapeProjector(projection);
		polygon.traverse(ringProjector);
		
		final Iterator<Shape> shapeIterator = ringProjector.getShapes().iterator();
		
		if (!shapeIterator.hasNext()) {
			throw new InvalidGeometryException("Empty polygon encountered");
		}
		
		final Shape outerShape = shapeIterator.next();
		
		final Area polygonArea = new Area(outerShape);
		
		while (shapeIterator.hasNext()) {
			final Shape innerShape = shapeIterator.next();
			polygonArea.subtract(new Area(innerShape));
		}
		
		shapes.add(polygonArea);
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
