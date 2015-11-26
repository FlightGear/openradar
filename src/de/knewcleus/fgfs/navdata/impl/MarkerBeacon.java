/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
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
package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IMarkerBeacon;
import de.knewcleus.fgfs.navdata.model.IRunwayEnd;

public class MarkerBeacon implements IMarkerBeacon {
	protected final Point2D geographicPosition;
	protected final float elevation;
	protected final Type type;
	protected final String airportID;
	protected final String runwayID;
	protected volatile IRunwayEnd runwayEnd = null;
	
	public MarkerBeacon(Point2D geographicPosition, float elevation, Type type,
			String airportID, String runwayID)
	{
		this.geographicPosition = geographicPosition;
		this.elevation = elevation;
		this.type = type;
		this.airportID = airportID;
		this.runwayID = runwayID;
	}
	
	@Override
	public Point2D getGeographicPosition() {
		return geographicPosition;
	}
	
	@Override
	public float getElevation() {
		return elevation;
	}
	
	@Override
	public Type getType() {
		return type;
	}
	
	@Override
	public String getAirportID() {
		return airportID;
	}
	
	@Override
	public String getRunwayID() {
		return runwayID;
	}
	
	
	@Override
	public String toString() {
		return String.format("%s marker %+10.6f %+11.6f elev %4fft %s RWY %s",
				type.toString(),
				geographicPosition.getY(),
				geographicPosition.getX(),
				elevation / Units.FT,
				airportID, runwayID);
	}

    public void setRunwayEnd(IRunwayEnd runwayEnd) {
        this.runwayEnd=runwayEnd;
    }

    public IRunwayEnd getRunwayEnd() {
        return runwayEnd;
    }
}
