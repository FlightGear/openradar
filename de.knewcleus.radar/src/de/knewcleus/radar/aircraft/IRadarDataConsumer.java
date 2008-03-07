package de.knewcleus.radar.aircraft;

public interface IRadarDataConsumer {
	public void radarTargetAcquired(IAircraft aircraft);
	public void radarTargetLost(IAircraft aircraft);
}
