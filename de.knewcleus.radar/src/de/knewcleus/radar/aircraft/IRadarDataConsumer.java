package de.knewcleus.radar.aircraft;

public interface IRadarDataConsumer {
	public void radarTargetAquired(IAircraft aircraft);
	public void radarTargetLost(IAircraft aircraft);
}
