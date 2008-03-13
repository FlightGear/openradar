package de.knewcleus.radar.ui.aircraft;

import java.util.ArrayDeque;
import java.util.Deque;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.aircraft.RadarTargetInformation;
import de.knewcleus.radar.aircraft.SSRMode;

public class AircraftState {
	protected final AircraftStateManager aircraftStateManager;
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>();
	protected double groundSpeed=0.0;
	protected double trueCourse=0.0;
	protected SSRMode ssrMode;
	protected String ssrCode;
	protected double pressureAltitude=0.0;
	protected boolean isSelected=false;
	protected AircraftTaskState taskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	public AircraftState(AircraftStateManager aircraftStateManager) {
		this.aircraftStateManager=aircraftStateManager;
	}
	
	public String getCallsign() {
		// FIXME: Use the SSR correlation database as soon as we have one
		return (ssrMode.hasSSRCode()?ssrCode:"****");
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
	
	public boolean canSelect() {
		return taskState!=AircraftTaskState.OTHER;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public AircraftTaskState getTaskState() {
		return taskState;
	}
	
	public void update(RadarTargetInformation targetInformation) {
		Position currentGeodPosition=new Position(targetInformation.getLongitude(),targetInformation.getLatitude(),0);
		positionBuffer.addLast(new Position(currentGeodPosition));
		/* We always keep at least the last cartesianPosition, so the limit is historyLength+1 */
		if (positionBuffer.size()>aircraftStateManager.getMaximumPositionBufferLength()) {
			positionBuffer.removeFirst();
		}
		
		groundSpeed=targetInformation.getGroundSpeed();
		trueCourse=targetInformation.getTrueCourse();
		ssrMode=targetInformation.getSSRMode();
		ssrCode=targetInformation.getSSRCode();
		pressureAltitude=targetInformation.getPressureAltitude();
	}
}
