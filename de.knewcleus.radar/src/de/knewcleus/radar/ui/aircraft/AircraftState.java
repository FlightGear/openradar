package de.knewcleus.radar.ui.aircraft;

import java.util.ArrayDeque;
import java.util.Deque;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.aircraft.IRadarTarget;

public class AircraftState {
	protected final AircraftStateManager aircraftStateManager;
	protected final IRadarTarget associatedTarget;
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>();
	protected double pressureAltitude=0.0;
	protected double groundSpeed=0.0;
	protected double trueCourse=0.0;
	protected boolean isSelected=false;
	protected AircraftTaskState taskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	public AircraftState(AircraftStateManager aircraftStateManager, IRadarTarget aircraft) {
		this.aircraftStateManager=aircraftStateManager;
		this.associatedTarget=aircraft;
	}
	
	public IRadarTarget getAircraft() {
		return associatedTarget;
	}
	
	public String getCallsign() {
		// FIXME: Use the SSR correlation database as soon as we have one
		return (associatedTarget.hasSSRCode()?associatedTarget.getSSRCode():"****");
	}
	
	public Deque<Position> getPositionBuffer() {
		return positionBuffer;
	}
	
	public Position getPosition() {
		return positionBuffer.getLast();
	}
	
	public double getPressureAltitude() {
		return pressureAltitude;
	}
	
	public double getGroundSpeed() {
		return groundSpeed;
	}
	
	public double getTrueCourse() {
		return trueCourse;
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
	
	public void update() {
		Position currentGeodPosition=associatedTarget.getPosition();
		positionBuffer.addLast(new Position(currentGeodPosition));
		/* We always keep at least the last cartesianPosition, so the limit is historyLength+1 */
		if (positionBuffer.size()>aircraftStateManager.getMaximumPositionBufferLength()) {
			positionBuffer.removeFirst();
		}
		
		groundSpeed=associatedTarget.getGroundSpeed();
		trueCourse=associatedTarget.getTrueCourse();
		pressureAltitude=associatedTarget.getPressureAltitude();
	}
}
