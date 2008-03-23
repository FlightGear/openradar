package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.radar.aircraft.AircraftState;
import de.knewcleus.radar.aircraft.AircraftTaskState;
import de.knewcleus.radar.targets.SSRMode;
import de.knewcleus.radar.targets.Track;

public class Aircraft implements IVehicle {
	protected final VehicleManager vehicleManager;
	protected final Track track;
	protected boolean isSelected=false;
	
	public Aircraft(VehicleManager aircraftManager, Track track) {
		this.vehicleManager=aircraftManager;
		this.track=track;
	}
	
	public VehicleManager getAircraftManager() {
		return vehicleManager;
	}
	
	public Track getTrack() {
		return track;
	}
	
	public String getCallsign() {
		SSRMode ssrMode=track.getSSRMode();
		
		if (!ssrMode.hasSSRCode()) {
			return null;
		}
		
		String squawk=track.getSSRCode();
		String callsign=vehicleManager.getRadarWorkstation().getCorrelationDatabase().correlateToCallsign(squawk);
		
		return callsign;
	}
	
	public AircraftState getAircraftState() {
		String callsign=getCallsign();
		if (callsign==null)
			return null;
		
		return vehicleManager.getRadarWorkstation().getAircraftStateManager().getAircraftStateForCallsign(callsign);
	}
	
	public boolean isCorrelated() {
		return getCallsign()!=null;
	}
	
	public boolean isSelected() {
		return isSelected;
	}
	
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	
	public boolean canSelect() {
		AircraftState aircraftState=getAircraftState();
		if (aircraftState==null)
			return false;
		return aircraftState.getTaskState()!=AircraftTaskState.OTHER;
	}
}
