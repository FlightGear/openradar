package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.ILocalizer;

public class Localizer extends AbstractILSComponent implements ILocalizer {
	protected final float trueHeading;
	
	public Localizer(Point2D geographicPosition, float elevation,
			String identification, IFrequency frequency, float range,
			String airportID, String runwayID, float trueHeading) {
		super(geographicPosition, elevation, identification, frequency, range, airportID, runwayID);
		this.trueHeading = trueHeading;
	}

	@Override
	public float getTrueHeading() {
		return trueHeading;
	}
	
	@Override
	public String toString() {
		return String.format("LOC %3s %+10.6f %+11.6f elev %4fft freq %s heading %+6.1f range %3fNM %s RWY %s",
				identification,
				geographicPosition.getY(),
				geographicPosition.getX(),
				elevation / Units.FT,
				frequency.toString(),
				trueHeading / Units.DEG,
				range / Units.NM,
				airportID, runwayID);
	}
}
