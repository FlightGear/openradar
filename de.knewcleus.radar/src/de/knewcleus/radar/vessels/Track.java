package de.knewcleus.radar.vessels;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

import de.knewcleus.fgfs.location.Position;

public class Track {
	public static class PositionBacklogEntry {
		protected final double timestamp;
		protected final Position position;
		
		public PositionBacklogEntry(double timestamp, Position position) {
			this.timestamp=timestamp;
			this.position=position;
		}
		
		public double getTimestamp() {
			return timestamp;
		}
		
		public Position getPosition() {
			return position;
		}
	}
	protected final static Logger logger=Logger.getLogger(Track.class.getName());
	protected final TrackManager trackManager;
	protected final Deque<PositionBacklogEntry> positionBacklog=new ArrayDeque<PositionBacklogEntry>();
	protected double groundSpeed=0.0;
	protected double trueCourse=0.0;
	protected SSRMode ssrMode;
	protected String ssrCode;
	protected double pressureAltitude=0.0;
	protected Vessel associatedVessel=null;
	
	/**
	 * Maximum position backlog in seconds.
	 * 
	 * This value gives the maximum timestamp difference between the oldest and the newest
	 * entry in the position backlog in seconds. On each update the oldest entries exceeding this
	 * difference are removed.
	 */
	protected static double maximumPositionBacklogSecs=3600.0;
	
	public Track(TrackManager trackManager) {
		this.trackManager=trackManager;
	}
	
	public Deque<PositionBacklogEntry> getPositionBacklog() {
		return positionBacklog;
	}
	
	public Position getPosition() {
		return positionBacklog.getLast().getPosition();
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
		final Position currentGeodPosition=new Position(targetInformation.getLongitude(),targetInformation.getLatitude(),0);
		final PositionBacklogEntry entry=new PositionBacklogEntry(targetInformation.getTimestamp(), currentGeodPosition);
		positionBacklog.addLast(entry);
		final double newestTimestamp=targetInformation.getTimestamp();
		
		/* Clean up the backlog */
		while (positionBacklog.getFirst().getTimestamp()<newestTimestamp-maximumPositionBacklogSecs) {
			positionBacklog.removeFirst();
		}
		
		groundSpeed=targetInformation.getGroundSpeed();
		trueCourse=targetInformation.getTrueCourse();
		ssrMode=targetInformation.getSSRMode();
		ssrCode=targetInformation.getSSRCode();
		pressureAltitude=targetInformation.getPressureAltitude();
	}
}
