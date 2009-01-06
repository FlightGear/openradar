package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.ITransmitter;

public abstract class AbstractTransmitter extends Intersection implements ITransmitter {

	protected final float elevation;
	protected final IFrequency frequency;
	protected final float range;

	public AbstractTransmitter(Point2D geographicPosition,
			float elevation, String identification, IFrequency frequency, float range)
	{
		super(geographicPosition, identification);
		this.elevation = elevation;
		this.frequency = frequency;
		this.range = range;
	}
	
	@Override
	public float getElevation() {
		return elevation;
	}

	@Override
	public IFrequency getFrequency() {
		return frequency;
	}

	@Override
	public float getRange() {
		return range;
	}

}