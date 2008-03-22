package de.knewcleus.radar.aircraft;

public class AircraftState {
	protected final String callsign;
	protected AircraftTaskState taskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	public AircraftState(String callsign) {
		this.callsign=callsign;
	}
	
	public AircraftTaskState getTaskState() {
		return taskState;
	}
}
