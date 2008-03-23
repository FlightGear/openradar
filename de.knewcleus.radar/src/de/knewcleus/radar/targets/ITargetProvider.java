package de.knewcleus.radar.targets;

/**
 * A target provider provides position data on radar targets, regularly updating the data.
 * @author Ralf Gerlich
 */
public interface ITargetProvider {
	public abstract int getSecondsBetweenUpdates();
	public abstract void registerTrackDataConsumer(ITrackDataConsumer consumer);
	public abstract void unregisterTrackDataConsumer(ITrackDataConsumer consumer);
}
