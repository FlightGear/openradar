package de.knewcleus.radar.ui.aircraft;

import java.util.ArrayDeque;
import java.util.Deque;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.aircraft.IAircraft;

public class AircraftState {
	protected final AircraftStateManager aircraftStateManager;
	protected final IAircraft aircraft;
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>();
	protected double pressureAltitude=0.0;
	protected double groundSpeed=0.0;
	protected double trueCourse=0.0;
	protected boolean isSelected=false;
	protected AircraftTaskState taskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	public AircraftState(AircraftStateManager aircraftStateManager, IAircraft aircraft) {
		this.aircraftStateManager=aircraftStateManager;
		this.aircraft=aircraft;
	}
	
	public IAircraft getAircraft() {
		return aircraft;
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
		Position currentGeodPosition=aircraft.getPosition();
		positionBuffer.addLast(new Position(currentGeodPosition));
		/* We always keep at least the last cartesianPosition, so the limit is historyLength+1 */
		if (positionBuffer.size()>aircraftStateManager.getMaximumPositionBufferLength()) {
			positionBuffer.removeFirst();
		}
		
		groundSpeed=aircraft.getGroundSpeed();
		trueCourse=aircraft.getTrueCourse();
		pressureAltitude=aircraft.getPressureAltitude();
	}
}
