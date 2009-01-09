package de.knewcleus.fgfs.navdata.xplane;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IAerodrome;
import de.knewcleus.fgfs.navdata.model.ILandingSurface;
import de.knewcleus.fgfs.navdata.model.INavPoint;
import de.knewcleus.fgfs.navdata.model.SurfaceType;

public class LandingSurface implements INavPoint, ILandingSurface {
	protected IAerodrome aerodrome;
	protected final SurfaceType surfaceType;
	protected final float length;
	protected final float width;
	protected final Point2D geographicCenter;
	protected final float trueHeading;
	protected final String designation;
	
	public LandingSurface(SurfaceType surfaceType, float length, float width,
			Point2D geographicCenter, float trueHeading, String designation) {
		this.surfaceType = surfaceType;
		this.length = length;
		this.width = width;
		this.geographicCenter = geographicCenter;
		this.trueHeading = trueHeading;
		this.designation = designation;
	}
	
	@Override
	public Point2D getGeographicPosition() {
		return geographicCenter;
	}

	protected void setAerodrome(IAerodrome aerodrome) {
		this.aerodrome = aerodrome;
	}

	public IAerodrome getAerodrome() {
		return aerodrome;
	}

	public String getAirportID() {
		return aerodrome.getIdentification();
	}

	public SurfaceType getSurfaceType() {
		return surfaceType;
	}

	public float getLength() {
		return length;
	}

	public float getWidth() {
		return width;
	}

	public Point2D getGeographicCenter() {
		return geographicCenter;
	}

	public float getTrueHeading() {
		return trueHeading;
	}

	public String getDesignation() {
		return designation;
	}

}