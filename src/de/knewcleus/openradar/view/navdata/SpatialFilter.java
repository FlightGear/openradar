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
package de.knewcleus.openradar.view.navdata;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.navdata.INavDatumFilter;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.model.INavDatum;
import de.knewcleus.fgfs.navdata.model.INavPoint;

public class SpatialFilter implements INavDatumFilter<INavDatum> {
	protected final Rectangle2D bounds;
	
	public SpatialFilter(Rectangle2D bounds) {
		this.bounds = bounds;
	}
	
	@Override
	public boolean allow(INavDatum datum) {
		if (datum instanceof INavPoint) {
			final INavPoint point = (INavPoint) datum;
			return bounds.contains(point.getGeographicPosition());
		} else if (datum instanceof IAirwaySegment) {
			final IAirwaySegment segment = (IAirwaySegment) datum;
			final IIntersection start, end;
			start = segment.getStartPoint();
			end = segment.getEndPoint();
			final Line2D line = new Line2D.Double(start.getGeographicPosition(), end.getGeographicPosition());
			return line.intersects(bounds);
		} 
		return true;
	}
}