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
		return String.format("RWY End %s %s (%+10.6f,%+11.6f) hdg %05.1f stopway %3.0fft TORA %5.0fft LDA %5.0fft",
				runway.getAirportID(),
				runwayID,
				geographicPosition.getY(),
				geographicPosition.getX(),
				trueHeading / Units.DEG,
				stopwayLength/Units.FT,
				tora / Units.FT,
				lda / Units.FT);
	}
}
