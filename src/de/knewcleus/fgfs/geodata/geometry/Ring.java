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
package de.knewcleus.fgfs.geodata.geometry;

import java.util.Iterator;

import de.knewcleus.fgfs.geodata.GeodataException;


public class Ring extends LineString {
	public void closeRing() {
		if (!isClosed()) {
			final Point firstPoint=getContainedGeometry().get(0);
			add(firstPoint);
		}
	}
	
	public boolean isClosed() {
		if (getContainedGeometry().size()<2)
			return false;
		final Point firstPoint=getContainedGeometry().get(0);
		final Point lastPoint=getContainedGeometry().get(getContainedGeometry().size()-1);
		
		/* Yes, we actually want to compare for equality, not only within a given epsilon range */
		return (lastPoint.getX()==firstPoint.getX() &&
				lastPoint.getY()==firstPoint.getY() &&
				lastPoint.getZ()==firstPoint.getZ());
	}
	
	public double getEnclosedArea() {
		if (getPoints().size()<(2+(isClosed()?1:0)))
			return 0;
		double area=0.0;
		final Iterator<Point> pointIterator=iterator();
		final Point firstPoint=pointIterator.next();
		Point previousPoint=firstPoint;
		
		while (pointIterator.hasNext()) {
			final Point thisPoint=pointIterator.next();
			area+=(thisPoint.getX()-previousPoint.getX())*(thisPoint.getY()+previousPoint.getY())/2.0;
			previousPoint=thisPoint;
		}
		
		if (!isClosed()) {
			area+=(firstPoint.getX()-previousPoint.getX())*(firstPoint.getY()+previousPoint.getY())/2.0;
		}
		
		return area;
	}
	
	public boolean isClockWise() {
		return getEnclosedArea()>0.0;
	}
	
	@Override
	public void accept(IGeometryVisitor visitor) throws GeodataException {
		visitor.visit(this);
	}
}
