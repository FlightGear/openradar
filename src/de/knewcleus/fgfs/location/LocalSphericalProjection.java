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
package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;

public class LocalSphericalProjection implements IMapProjection {
	protected Position center;
	protected double lonwidth;
	
	public LocalSphericalProjection(Position center) {
		this.center=center;
		lonwidth=60.0*Units.NM*Math.cos(Math.toRadians(center.getY()));
	}

	@Override
	public Point2D forward(Position pos) {
		double x=(pos.getX()-center.getX())*lonwidth;
		double y=(pos.getY()-center.getY())*60.0*Units.NM;
		return new Point2D.Double(x, y);
	}

	@Override
	public Position backward(Point2D pos) {
		double lat=pos.getY()/60.0/Units.NM+center.getX();
		double lon=pos.getX()/lonwidth+center.getY();
		return new Position(lon,lat,0.0);
	}

}
