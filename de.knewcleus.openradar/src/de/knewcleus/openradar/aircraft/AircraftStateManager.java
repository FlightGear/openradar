package de.knewcleus.openradar.aircraft;

import java.util.HashMap;
import java.util.Map;

public class AircraftStateManager {
	protected final Map<String, AircraftState> aircraftStateMap=new HashMap<String, AircraftState>();
	
	public AircraftState getAircraftStateForCallsign(String callsign) {
		if (aircraftStateMap.containsKey(callsign)) {
			return aircraftStateMap.get(callsign);
		}
		
		AircraftState aircraftState=new AircraftState(callsign);
		aircraftStateMap.put(callsign, aircraftState);
		return aircraftState;
	}
}
