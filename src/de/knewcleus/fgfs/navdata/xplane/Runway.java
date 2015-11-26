/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navdata.xplane;

import static de.knewcleus.fgfs.location.Ellipsoid.WGS84;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;
import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.fgfs.navdata.model.IRunway;
import de.knewcleus.fgfs.navdata.model.IRunwayEnd;
import de.knewcleus.fgfs.navdata.model.SurfaceType;

public class Runway extends LandingSurface implements IRunway {
	protected final static GeodesicUtils geodesicUtils = new GeodesicUtils(WGS84);
	protected final RunwayEnd endA, endB;
	protected volatile IRunwayEnd startSide = null;
	protected volatile IRunwayEnd landSide = null;
	
	
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
	
	/**
	 *  Contructor for XPlane 10 format 
	 *  
	 */
    public Runway(SurfaceType surfaceType, float length, float width, 
                  Point2D geographicCenter, Point2D rwEndACenter, Point2D rwEndBCenter, float trueHeading,
                  String runwayEndNumberA, String runwayEndNumberB,
                  float endAThresholdLength, float endBThresholdLength) {
              super(surfaceType, length, width, geographicCenter, trueHeading, runwayEndNumberA);
              
              final float oppositeHeading;
              oppositeHeading = (
                      trueHeading>180.0f*Units.DEG?
                      trueHeading-180.0f*Units.DEG:
                          trueHeading+180.0f*Units.DEG);
              endA = new RunwayEnd(rwEndACenter, this, runwayEndNumberA, trueHeading, 0, endAThresholdLength, length-endBThresholdLength, length-endAThresholdLength);
              endB = new RunwayEnd(rwEndBCenter, this, runwayEndNumberB, oppositeHeading, 0, endBThresholdLength, length-endAThresholdLength, length-endBThresholdLength);
              
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

    @Override
    public synchronized void setStartSide(IRunwayEnd rwe) {
        this.startSide=rwe;
    }

    @Override
    public synchronized IRunwayEnd getStartSide() {
        return startSide;
    }

    @Override
    public synchronized void setLandSide(IRunwayEnd rwe) {
        this.landSide=rwe;
        
    }

    @Override
    public synchronized IRunwayEnd getLandSide() {
        return landSide;
    }
}
