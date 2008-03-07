package de.knewcleus.radar.aircraft;

public interface IRadarDataProvider<T extends IAircraft> extends Iterable<T> {
	public abstract void registerRadarDataConsumer(IRadarDataConsumer consumer);
	public abstract void unregisterRadarDataConsumer(IRadarDataConsumer consumer);
}
