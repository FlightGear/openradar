package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IDME;
import de.knewcleus.fgfs.navdata.model.IFrequency;

public class DME extends AbstractNamedTransmitter implements IDME {
	protected final float distanceBias;

	public DME(Point2D geographicPosition, float elevation,
			String identification, String name, IFrequency frequency, float range, float distanceBias) {
		super(geographicPosition, elevation, identification, name, frequency, range);
		this.distanceBias = distanceBias;
	}

	@Override
	public float getDistanceBias() {
		return distanceBias;
	}
	
	@Override
	public String toString() {
		return String.format("DME %3s %+10.6f %+11.6f elev %4fft freq %s bias %+6.1fNM range %3fNM name %s",
				identification,
				geographicPosition.getY(),
				geographicPosition.getX(),
				elevation / Units.FT,
				frequency.toString(),
				distanceBias / Units.NM,
				range / Units.NM,
				name);
	}
}
