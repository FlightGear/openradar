package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.radar.aircraft.AircraftState;
import de.knewcleus.radar.aircraft.AircraftTaskState;
import de.knewcleus.radar.targets.SSRMode;
import de.knewcleus.radar.targets.Track;

public class Aircraft implements IVehicle {
	protected final VehicleManager aircraftManager;
	protected final Track target;
	protected boolean isSelected=false;
	
	public Aircraft(VehicleManager aircraftManager, Track target) {
		this.aircraftManager=aircraftManager;
		this.target=target;
	}
	
	public VehicleManager getAircraftManager() {
		return aircraftManager;
	}
	
	public Track getTarget() {
		return target;
	}
	
	public String getCallsign() {
		SSRMode ssrMode=target.getSSRMode();
		
		if (!ssrMode.hasSSRCode()) {
			return "****";
		}
		
		String squawk=target.getSSRCode();
		
		String callsign=aircraftManager.getRadarWorkstation().getCorrelationDatabase().correlateToCallsign(squawk);
		if (callsign!=null)
			return callsign;
		
		return squawk;
	}
	
	public AircraftState getAircraftState() {
		if (!target.getSSRMode().hasSSRCode())
			return null;
		String squawk=target.getSSRCode();
		String callsign=aircraftManager.getRadarWorkstation().getCorrelationDatabase().correlateToCallsign(squawk);
		
		if (callsign==null)
			return null;
		
		return aircraftManager.getRadarWorkstation().getAircraftStateManager().getAircraftStateForCallsign(callsign);
	}
	
	public boolean isCorrelated() {
		if (!target.getSSRMode().hasSSRCode())
			return false;
		String squawk=target.getSSRCode();
		String callsign=aircraftManager.getRadarWorkstation().getCorrelationDatabase().correlateToCallsign(squawk);
		
		return callsign!=null;
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
