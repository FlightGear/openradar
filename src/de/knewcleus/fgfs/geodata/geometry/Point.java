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

import de.knewcleus.fgfs.geodata.GeodataException;

public class Point extends Geometry {
	protected final double x, y, z, m;
	protected final boolean hasZ;
	protected final boolean hasM;
	
	public Point(double x, double y) {
		this.x=x;
		this.y=y;
		this.z=0;
		this.m=0;
		this.hasZ=false;
		this.hasM=false;
	}
	
	public Point(double x, double y, double z) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.m=0;
		this.hasZ=true;
		this.hasM=false;
	}
	
	public Point(double x, double y, double z, double m, boolean hasZ, boolean hasM) {
		this.x=x;
		this.y=y;
		this.z=z;
		this.m=m;
		this.hasZ=hasZ;
		this.hasM=hasM;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public double getM() {
		return m;
	}
	
	public boolean hasZ() {
		return hasZ;
	}
	
	public boolean hasM() {
		return hasM;
	}
	
	@Override
	public double getXMax() {
		return x;
	}

	@Override
	public double getXMin() {
		return x;
	}

	@Override
	public double getYMax() {
		return y;
	}

	@Override
	public double getYMin() {
		return y;
	}

	@Override
	public double getZMax() {
		return z;
	}

	@Override
	public double getZMin() {
		return z;
	}
	
	@Override
	public void accept(IGeometryVisitor visitor) throws GeodataException {
		visitor.visit(this);
	}
}
