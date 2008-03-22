package de.knewcleus.radar.ui.aircraft;

import java.util.Set;

public interface IAircraftUpdateListener {
	public abstract void aircraftUpdated(Set<Aircraft> updatedAircraft);
	public abstract void aircraftLost(Aircraft lostAircraft);
}
