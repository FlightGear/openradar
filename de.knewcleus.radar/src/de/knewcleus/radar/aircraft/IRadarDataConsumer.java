package de.knewcleus.radar.aircraft;

public interface IRadarDataConsumer<T extends IRadarTarget> {
	public void radarTargetAcquired(T aircraft);
	public void radarDataUpdated();
	public void radarTargetLost(T aircraft);
}
