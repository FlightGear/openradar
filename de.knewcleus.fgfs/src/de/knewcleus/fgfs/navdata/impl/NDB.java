package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.INDB;

public class NDB extends AbstractNamedTransmitter implements INDB {
	public NDB(Point2D geographicPosition, float elevation,
			String identification, String name, IFrequency frequency,
			float range)
	{
		super(geographicPosition, elevation, identification, name, frequency, range);
	}
	
	@Override
	public String toString() {
		return String.format("NDB %3s %+10.6f %+11.6f elev %4fft freq %s range %3fNM name %s",
				identification,
				geographicPosition.getY(),
				geographicPosition.getX(),
				elevation / Units.FT,
				frequency.toString(),
				range / Units.NM,
				name);
	}
}
