package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IAerodrome;

public class Aerodrome implements IAerodrome {
	protected final Point2D geographicPosition;
	protected final float elevation;
	protected final String identification;
	protected final String name;
	protected final Type type;
	
	public Aerodrome(Point2D geographicPosition, float elevation,
			String identification, String name, Type type) {
		this.geographicPosition = geographicPosition;
		this.elevation = elevation;
		this.identification = identification;
		this.name = name;
		this.type = type;
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
	public Type getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return String.format("Aerodrome %s (%s) %s (%+10.6f,%+11.6f) elev %4.0fft",
				identification,
				name,
				type.toString(),
				geographicPosition.getY() / Units.DEG,
				geographicPosition.getX() / Units.DEG,
				elevation / Units.FT);
	}
}
