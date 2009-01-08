package de.knewcleus.fgfs.navdata.impl;

import de.knewcleus.fgfs.navdata.model.IRunway;
import de.knewcleus.fgfs.navdata.model.SurfaceType;

public class Runway implements IRunway {
	protected final String airportID;
	protected final SurfaceType surfaceType;
	protected final float length;
	protected final float width;
	
	public Runway(String airportID, SurfaceType surfaceType, float length,
			float width) {
		this.airportID = airportID;
		this.surfaceType = surfaceType;
		this.length = length;
		this.width = width;
	}

	@Override
	public String getAirportID() {
		return airportID;
	}

	@Override
	public SurfaceType getSurfaceType() {
		return surfaceType;
	}

	@Override
	public float getLength() {
		return length;
	}

	@Override
	public float getWidth() {
		return width;
	}
}
