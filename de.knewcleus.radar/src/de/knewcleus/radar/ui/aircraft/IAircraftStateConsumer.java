package de.knewcleus.radar.ui.aircraft;

import java.util.Set;

public interface IAircraftStateConsumer {
	public abstract void aircraftStateUpdate(Set<AircraftState> aircraftState);
	public abstract void aircraftStateLost(AircraftState aircraftState);
}
