package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.IILSComponent;

public abstract class AbstractILSComponent extends AbstractTransmitter
	implements IILSComponent
{
	protected final String airportID;
	protected final String runwayID;

	public AbstractILSComponent(Point2D geographicPosition, float elevation,
			String identification, IFrequency frequency, float range,
			String airportID, String runwayID) {
		super(geographicPosition, elevation, identification, frequency, range);
		this.airportID = airportID;
		this.runwayID = runwayID;
	}

	@Override
	public String getAirportID() {
		return airportID;
	}

	@Override
	public String getRunwayID() {
		return runwayID;
	}

}
