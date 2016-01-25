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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.navdata.impl;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IRunway;
import de.knewcleus.fgfs.navdata.model.IRunwayEnd;

public class RunwayEnd implements IRunwayEnd {
	protected final Point2D geographicPosition;
	protected final IRunway runway;
	protected final String runwayID;
	protected final float trueHeading;
	protected final float stopwayLength;
	protected final float displacedThreshold;
	protected final float tora;
	protected final float lda;

    protected IRunwayEnd oppositeEnd;
    private Glideslope glideslope = null;
	
	public RunwayEnd(Point2D geographicPosition, IRunway runway,
			String runwayID, float trueHeading, float stopwayLength,
			float displacedThreshold, float tora, float lda) {
		this.geographicPosition = geographicPosition;
		this.runway = runway;
		this.runwayID = runwayID;
		this.trueHeading = trueHeading;
		this.stopwayLength = stopwayLength;
		this.displacedThreshold = displacedThreshold;
		this.tora = tora;
		this.lda = lda;
	}

	@Override
	public Point2D getGeographicPosition() {
		return geographicPosition;
	}
	
	@Override
	public IRunway getRunway() {
		return runway;
	}
	
	@Override
	public String getRunwayID() {
		return runwayID;
	}
	
	@Override
	public float getTrueHeading() {
		return trueHeading;
	}
	
	@Override
	public float getStopwayLength() {
		return stopwayLength;
	}
	
	@Override
	public float getDisplacedThreshold() {
		return displacedThreshold;
	}
	
	@Override
	public float getTORA() {
		return tora;
	}
	
	@Override
	public float getLDA() {
		return lda;
	}
	
	@Override
	public IRunwayEnd getOppositeEnd() {
		return oppositeEnd;
	}
	
	public void setOppositeEnd(IRunwayEnd oppositeEnd) {
		this.oppositeEnd = oppositeEnd;
	}
	
	@Override
	public String toString() {
		return String.format("RWY End %s %s %s (%+10.6f,%+11.6f) hdg %05.1f stopway %3.0fft TORA %5.0fft LDA %5.0fft",
				runway.getAirportID(),
				runwayID,
				runway.getSurfaceType(),
				geographicPosition.getY(),
				geographicPosition.getX(),
				trueHeading / Units.DEG,
				stopwayLength/Units.FT,
				tora / Units.FT,
				lda / Units.FT);
	}

    public boolean isActive() {
        return getRunway().getStartSide()==getOppositeEnd() || getRunway().getLandSide()==this;
    }

    public boolean isLandingActive() {
        return getRunway().getLandSide()==this;
    }

    public boolean isStartingActive() {
        return getRunway().getStartSide()==getOppositeEnd();
    }

    public Glideslope getGlideslope() {
        return glideslope;
    }

    public void setGlideslope(Glideslope glideslope) {
        this.glideslope = glideslope;
    }
}
