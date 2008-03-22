package de.knewcleus.radar.ui.aircraft;

import de.knewcleus.radar.aircraft.SSRMode;
import de.knewcleus.radar.aircraft.Target;

public class Aircraft {
	protected final AircraftManager aircraftManager;
	protected final Target target;
	protected boolean isSelected=false;
	
	public Aircraft(AircraftManager aircraftManager, Target target) {
		this.aircraftManager=aircraftManager;
		this.target=target;
	}
	
	public AircraftManager getAircraftManager() {
		return aircraftManager;
	}
	
	public Target getTarget() {
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
	
	public boolean isCorrelated() {
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
		// TODO: implement properly
		return true;
	}
}
