package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IIntersection;

public class Intersection implements IIntersection {
	protected final Point2D geographicPosition;
	protected final String identification;
	
	public Intersection(Point2D geographicPosition, String identification) {
		this.geographicPosition = geographicPosition;
		this.identification = identification;
	}

	@Override
	public Point2D getGeographicPosition() {
		return geographicPosition;
	}

	@Override
	public String getIdentification() {
		return identification;
	}
	
	@Override
	public String toString() {
		return String.format("FIX %+10.6f %+11.6f %s",
				geographicPosition.getY(),
				geographicPosition.getX(),
				identification);
	}
}
