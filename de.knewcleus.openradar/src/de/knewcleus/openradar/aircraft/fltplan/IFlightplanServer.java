package de.knewcleus.openradar.aircraft.fltplan;

import java.util.Set;

public interface IFlightplanServer {
	/**
	 * Get the available flightplans for the given callsign.
	 * 
	 * There may be multiple flightplans filed for a given callsign.
	 */
	public abstract Set<Flightplan> getAvailableFlightplans(String callsign);
	public abstract void updateFlightplan(Flightplan flightplan);
	public abstract void cancelFlightplan(Object reference);
	
	public abstract void openFlightplan(Object reference);
	public abstract Flightplan getOpenFlightplan(String callsign);
	public abstract void closeFlightplan(String callsign);
	
	public abstract void registerFlightplanListener(IFlightplanListener flightplanListener);
	public abstract void unregisterFlightplanListener(IFlightplanListener flightplanListener);
}
