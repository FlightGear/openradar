package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.IVOR;

public class VOR extends AbstractNamedTransmitter implements IVOR {
	protected final float variation;

	public VOR(Point2D geographicPosition, float elevation,
			String identification, String name, IFrequency frequency,
			float range, float variation) {
		super(geographicPosition, elevation, identification, name, frequency, range);
		this.variation = variation;
	}
	
	@Override
	public float getVariation() {
		return variation;
	}
	
	@Override
	public String toString() {
		return String.format("VOR %3s %+10.6f %+11.6f elev %4fft freq %s variation %+6.1f range %3fNM name %s",
				identification,
				geographicPosition.getY(),
				geographicPosition.getX(),
				elevation / Units.FT,
				frequency.toString(),
				variation / Units.DEG,
				range / Units.NM,
				name);
	}
}
