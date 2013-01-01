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
import java.awt.geom.Rectangle2D;


public class MapTransformationHelper {
	protected IMapProjection mapTransformation;
	
	public MapTransformationHelper(IMapProjection mapTransformation) {
		this.mapTransformation=mapTransformation;
	}
	
	public Rectangle2D toLocal(Position p0, Position p1) {
		Point2D localP0=mapTransformation.forward(p0);
		Point2D localP1=mapTransformation.forward(p1);
		
		double xmin,xmax,ymin,ymax;
		
		xmin=Math.min(localP0.getX(),localP1.getX());
		xmax=Math.max(localP0.getX(),localP1.getX());
		ymin=Math.min(localP0.getY(),localP1.getY());
		ymax=Math.max(localP0.getY(),localP1.getY());
		
		return new Rectangle2D.Double(xmin,ymin,xmax-xmin,ymax-ymin);
	}
}
