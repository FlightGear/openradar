package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IMarkerBeacon;

public class MarkerBeacon implements IMarkerBeacon {
	protected final Point2D geographicPosition;
	protected final float elevation;
	protected final Type type;
	protected final String airportID;
	protected final String runwayID;
	
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
}
