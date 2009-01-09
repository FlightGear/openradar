package de.knewcleus.fgfs.navdata.xplane;

import static de.knewcleus.fgfs.location.Ellipsoid.WGS84;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.fgfs.navdata.model.IRunway;
import de.knewcleus.fgfs.navdata.model.SurfaceType;

public class Runway extends LandingSurface implements IRunway {
	protected final static GeodesicUtils geodesicUtils = new GeodesicUtils(WGS84);
	protected final RunwayEnd endA, endB;
	
	public Runway(SurfaceType surfaceType, float length,
			float width, Point2D geographicCenter, float trueHeading,
			String designation,
			float endAThresholdLength, float endBThresholdLength,
			float endAStopwayLength, float endBStopwayLength) {
		super(surfaceType, length, width, geographicCenter, trueHeading, designation);
		
		final float oppositeHeading;
		oppositeHeading = (
				trueHeading>180.0f*Units.DEG?
				trueHeading-180.0f*Units.DEG:
					trueHeading+180.0f*Units.DEG);
		endA = getRunwayEnd(
				getDesignation(),
				trueHeading,
				endAStopwayLength,
				endAThresholdLength, endBThresholdLength);
		endB = getRunwayEnd(
				getOppositeEndDesignation(),
				oppositeHeading,
				endBStopwayLength,
				endBThresholdLength, endAThresholdLength);
		
		endA.setOppositeEnd(endB);
		endB.setOppositeEnd(endA);
	}
	
	public String getOppositeEndDesignation() {
		final String numberString = designation.substring(0,2);
		final int number = Integer.parseInt(numberString);
		final int oppositeNumber = (number>18?number-18:number+18);
		if (designation.length()<3) {
			return String.format("%02d",oppositeNumber);
		}
		final char side = designation.charAt(2);
		final char oppositeSide;
		switch (side) {
		case 'L':
			oppositeSide = 'R';
			break;
		case 'C':
			oppositeSide = 'C';
			break;
		case 'R':
			oppositeSide = 'L';
			break;
		case 'x':
		default:
			oppositeSide = 'x';
			break;
		}
		return String.format("%02d%c",oppositeNumber, oppositeSide);
	}
	
	protected RunwayEnd getRunwayEnd(String rwyID, float heading,
			float stopwayLength,
			float displacedThreshold, float oppositeDisplacedThreshold) {
		final GeodesicInformation information;
		information = geodesicUtils.direct(
				geographicCenter.getX(), geographicCenter.getY(),
				heading+180.0*Units.DEG,
				length/2.0f);
		final Point2D endPosition = new Point2D.Double(information.getEndLon(), information.getEndLat());
		return new RunwayEnd(
				endPosition,
				this,
				rwyID,
				heading,
				stopwayLength,
				displacedThreshold,
				length-oppositeDisplacedThreshold,
				length-displacedThreshold);
	}
	
	public RunwayEnd getEndA() {
		return endA;
	}
	
	public RunwayEnd getEndB() {
		return endB;
	}
}
