package de.knewcleus.radar.ui.aircraft;

import java.util.ArrayDeque;
import java.util.Deque;

import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.radar.aircraft.IAircraft;

public class AircraftState {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
	protected final AircraftStateManager aircraftStateManager;
	protected final IAircraft aircraft;
	protected final Deque<Position> positionBuffer=new ArrayDeque<Position>();
	protected boolean isSelected=false;
	protected Vector3D lastVelocityVector=new Vector3D();
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
	
	public boolean canSelect() {
		return taskState!=AircraftTaskState.OTHER;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public Vector3D getLastVelocityVector() {
		return lastVelocityVector;
	}
	
	public AircraftTaskState getTaskState() {
		return taskState;
	}
	
	public void update() {
		Position currentGeodPosition=aircraft.getPosition();
		Position currentGeocPosition=geodToCartTransformation.forward(currentGeodPosition);
		lastVelocityVector=aircraft.getVelocityVector();
		
		positionBuffer.addLast(new Position(currentGeocPosition));
		/* We always keep at least the last cartesianPosition, so the limit is historyLength+1 */
		if (positionBuffer.size()>aircraftStateManager.getMaximumPositionBufferLength()) {
			positionBuffer.removeFirst();
		}
	}
}
