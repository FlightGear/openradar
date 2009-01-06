package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.IGlideslope;

public class Glideslope extends AbstractILSComponent implements IGlideslope {
	protected final float glideslopeAngle;

	public Glideslope(Point2D geographicPosition, float elevation,
			String identification, IFrequency frequency, float range,
			String airportID, String runwayID, float glideslopeAngle) {
		super(geographicPosition, elevation, identification, frequency, range,
				airportID, runwayID);
		this.glideslopeAngle = glideslopeAngle;
	}

	@Override
	public float getGlideslopeAngle() {
		return glideslopeAngle;
	}
	
	@Override
	public String toString() {
		return String.format("GS %3s %+10.6f %+11.6f elev %4fft freq %s angle %+6.1f range %3fNM %s RWY %s",
				identification,
				geographicPosition.getY(),
				geographicPosition.getX(),
				elevation / Units.FT,
				frequency.toString(),
				glideslopeAngle / Units.DEG,
				range / Units.NM,
				airportID, runwayID);
	}

}
