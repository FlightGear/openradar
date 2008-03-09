package de.knewcleus.radar.aircraft;

public interface IRadarDataConsumer<T extends IAircraft> {
	public void radarTargetAcquired(T aircraft);
	public void radarDataUpdated();
	public void radarTargetLost(T aircraft);
}
