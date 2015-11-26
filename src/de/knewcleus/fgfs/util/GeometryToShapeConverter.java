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
package de.knewcleus.fgfs.util;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;

import de.knewcleus.fgfs.geodata.geometry.Geometry;
import de.knewcleus.fgfs.geodata.geometry.Point;
import de.knewcleus.fgfs.geodata.geometry.Polygon;
import de.knewcleus.fgfs.geodata.geometry.Ring;

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
