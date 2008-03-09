package de.knewcleus.radar.aircraft;

public interface IAircraftStateConsumer {
	public abstract void aircraftStateAcquired(AircraftState aircraftState);
	public abstract void aircraftStateUpdate();
	public abstract void aircraftStateLost(AircraftState aircraftState);
}
