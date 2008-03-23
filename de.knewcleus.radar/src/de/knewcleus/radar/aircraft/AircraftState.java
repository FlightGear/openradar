package de.knewcleus.radar.aircraft;

import java.util.Collections;
import java.util.List;

import javax.swing.Action;

public class AircraftState {
	protected final String callsign;
	protected AircraftTaskState taskState=AircraftTaskState.ASSUMED; // FIXME: this should actually be set explicitly
	
	public AircraftState(String callsign) {
		this.callsign=callsign;
	}
	
	public AircraftTaskState getTaskState() {
		return taskState;
	}
	
	public List<Action> getAvailableActions() {
		return Collections.emptyList();
	}
}
