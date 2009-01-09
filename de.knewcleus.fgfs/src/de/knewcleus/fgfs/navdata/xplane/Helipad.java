package de.knewcleus.fgfs.navdata.xplane;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IHelipad;
import de.knewcleus.fgfs.navdata.model.SurfaceType;

public class Helipad extends LandingSurface implements IHelipad {
	public Helipad(SurfaceType surfaceType, float length, float width,
			Point2D geographicCenter, float trueHeading, String designation) {
		super(surfaceType, length, width, geographicCenter, trueHeading,
				designation);
	}
	
	@Override
	public String toString() {
		return String.format("Helipad %s %s %s (%+10.6f,%+11.6f) hdg %05.1f",
				getAirportID(),
				designation,
				surfaceType,
				getGeographicPosition().getY(),
				getGeographicPosition().getX(),
				trueHeading / Units.DEG);
	}
}
