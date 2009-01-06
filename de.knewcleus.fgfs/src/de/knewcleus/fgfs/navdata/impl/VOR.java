package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;
import de.knewcleus.fgfs.navdata.model.IVOR;

public class VOR implements IVOR {
	protected final Point2D geographicPosition;
	protected final float elevation;
	protected final String identification;
	protected final String name;
	protected final IFrequency frequency;
	protected final float range;
	protected final float variation;

	public VOR(Point2D geographicPosition, float elevation,
			String identification, String name, IFrequency frequency,
			float range, float variation) {
		this.geographicPosition = geographicPosition;
		this.elevation = elevation;
		this.identification = identification;
		this.name = name;
		this.frequency = frequency;
		this.range = range;
		this.variation = variation;
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
	public String getIdentification() {
		return identification;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public IFrequency getFrequency() {
		return frequency;
	}
	
	@Override
	public float getRange() {
		return range;
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
