package de.knewcleus.radar.vessels;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

import de.knewcleus.fgfs.location.Position;

public class Track {
	protected final static Logger logger=Logger.getLogger(Track.class.getName());
	protected final TrackManager trackManager;
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>();
	protected double groundSpeed=0.0;
	protected double trueCourse=0.0;
	protected SSRMode ssrMode;
	protected String ssrCode;
	protected double pressureAltitude=0.0;
	protected Vessel associatedVessel=null;
	
	protected static int maximumPositionBufferLength=1200;
	
	public Track(TrackManager trackManager) {
		this.trackManager=trackManager;
	}
	
	public Deque<Position> getPositionBuffer() {
		return positionBuffer;
	}
	
	public Position getPosition() {
		return positionBuffer.getLast();
	}
	
	public double getGroundSpeed() {
		return groundSpeed;
	}
	
	public double getTrueCourse() {
		return trueCourse;
	}
	
	public SSRMode getSSRMode() {
		return ssrMode;
	}
	
	public String getSSRCode() {
		return ssrCode;
	}
	
	public double getPressureAltitude() {
		return pressureAltitude;
	}
	
	public Vessel getAssociatedVessel() {
		return associatedVessel;
	}
	
	public int getFlightLevel() {
		return (int)Math.round(pressureAltitude/100.0);
	}
	
	public void update(PositionUpdate targetInformation) {
		// TODO: update associated vessel if any
		logger.fine("Updating aircraft state "+this+" from "+targetInformation);
		Position currentGeodPosition=new Position(targetInformation.getLongitude(),targetInformation.getLatitude(),0);
		positionBuffer.addLast(new Position(currentGeodPosition));
		/* We always keep at least the last cartesianPosition, so the limit is historyLength+1 */
		if (positionBuffer.size()>maximumPositionBufferLength) {
			positionBuffer.removeFirst();
		}
		
		groundSpeed=targetInformation.getGroundSpeed();
		trueCourse=targetInformation.getTrueCourse();
		ssrMode=targetInformation.getSSRMode();
		ssrCode=targetInformation.getSSRCode();
		pressureAltitude=targetInformation.getPressureAltitude();
	}
}
