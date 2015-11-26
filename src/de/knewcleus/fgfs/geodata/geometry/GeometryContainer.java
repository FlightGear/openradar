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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.geodata.GeodataException;

public abstract class GeometryContainer<T extends Geometry> extends Geometry implements Iterable<T> {
	protected double xMin, xMax, yMin, yMax, zMin, zMax;
	
	protected final List<T> containedGeometry=new ArrayList<T>();
	
	public List<T> getContainedGeometry() {
		return Collections.unmodifiableList(containedGeometry);
	}
	
	public void add(T geometry) {
		if (containedGeometry.isEmpty()) {
			xMin=geometry.getXMin();
			xMax=geometry.getXMax();
			yMin=geometry.getYMin();
			yMax=geometry.getYMax();
			zMin=geometry.getZMin();
			zMax=geometry.getZMax();
		} else {
			xMin=Math.min(xMin, geometry.getXMin());
			xMax=Math.max(xMax, geometry.getXMax());
			yMin=Math.min(yMin, geometry.getYMin());
			yMax=Math.max(yMax, geometry.getYMax());
			zMin=Math.min(zMin, geometry.getZMin());
			zMax=Math.max(zMax, geometry.getZMax());
		}
		containedGeometry.add(geometry);
	}
	
	@Override
	public double getXMax() {
		return xMax;
	}

	@Override
	public double getXMin() {
		return xMin;
	}

	@Override
	public double getYMax() {
		return yMax;
	}

	@Override
	public double getYMin() {
		return yMin;
	}

	@Override
	public double getZMax() {
		return zMax;
	}

	@Override
	public double getZMin() {
		return zMin;
	}
	
	@Override
	public Iterator<T> iterator() {
		return getContainedGeometry().iterator();
	}
	
	public void traverse(IGeometryVisitor visitor) throws GeodataException {
		for (T child: containedGeometry) {
			child.accept(visitor);
		}
	}
}
