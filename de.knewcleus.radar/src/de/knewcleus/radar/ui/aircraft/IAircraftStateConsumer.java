package de.knewcleus.radar.ui.aircraft;

import java.util.Set;

import de.knewcleus.radar.aircraft.Target;

public interface IAircraftStateConsumer {
	public abstract void aircraftStateUpdate(Set<Target> aircraftState);
	public abstract void aircraftStateLost(Target aircraftState);
}
