package de.knewcleus.radar.aircraft;

/**
 * A radar data provider provides position data on radar targets, regularly updating the data.
 * @author Ralf Gerlich
 *
 * @param <T> The type of the radar targets
 */
public interface IRadarDataProvider<T extends IRadarTarget> extends Iterable<T> {
	public abstract int getSecondsBetweenUpdates();
	public abstract void registerRadarDataConsumer(IRadarDataConsumer<? super T> consumer);
	public abstract void unregisterRadarDataConsumer(IRadarDataConsumer<? super T> consumer);
}
