package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.INamedNavDatum;

public abstract class AbstractNamedTransmitter extends AbstractTransmitter implements INamedNavDatum {
	protected final String name;

	public AbstractNamedTransmitter(Point2D geographicPosition, float elevation,
			String identification, String name, IFrequency frequency, float range) {
		super(geographicPosition, elevation, identification, frequency, range);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}