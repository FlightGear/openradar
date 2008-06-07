package de.knewcleus.fgfs.util;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import de.knewcleus.fgfs.geodata.Geometry;
import de.knewcleus.fgfs.geodata.Point;
import de.knewcleus.fgfs.geodata.Polygon;
import de.knewcleus.fgfs.geodata.Ring;

public class GeometryToShapeConverter {
	public Shape convert(Geometry geometry) throws GeometryConversionException {
		final Class<? extends Geometry> geometryClass = geometry.getClass();
		try {
			final Method method=this.getClass().getMethod("doConvert", geometryClass);
			return (Shape)method.invoke(this, geometry);
		} catch (SecurityException e) {
			throw new GeometryConversionException(geometry, e);
		} catch (NoSuchMethodException e) {
			throw new GeometryConversionException(geometry, "Unable to convert unknown geometry type:"+geometryClass, e);
		} catch (IllegalArgumentException e) {
			throw new GeometryConversionException(geometry, e);
		} catch (IllegalAccessException e) {
			throw new GeometryConversionException(geometry, e);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof GeometryConversionException) {
				throw (GeometryConversionException)e.getCause();
			}
			throw new GeometryConversionException(geometry, e);
		}
	}
	
	public Shape doConvert(Polygon polygon) throws GeometryConversionException {
		Iterator<Ring> contourIterator=polygon.iterator();
		
		if (!contourIterator.hasNext())
			return new Area();
		
		Ring contour=contourIterator.next();
		
		Shape mainShape=convert(contour);
		
		Area area=new Area(mainShape);
		
		while (contourIterator.hasNext()) {
			contour=contourIterator.next();
			
			Shape holeShape=convert(contour);
			Area hole=new Area(holeShape);
			area.subtract(hole);
		}
		
		return area;
	}
	
	public Shape doConvert(Ring ring) throws GeometryConversionException {
		Path2D path=new Path2D.Double();
		
		Iterator<Point> posIterator=ring.iterator();
		
		if (!posIterator.hasNext())
			return path;
		
		Point point=posIterator.next();
		
		path.moveTo(point.getX(), point.getY());
		
		while (posIterator.hasNext()) {
			point=posIterator.next();
			path.lineTo(point.getX(), point.getY());
		}
		
		path.closePath();
		
		return path;
	}
}
