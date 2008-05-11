package de.knewcleus.radar.vessels;

/**
 * A position data provider provides position data on radar targets, regularly updating the data.
 * @author Ralf Gerlich
 */
public interface IPositionDataProvider {
	public abstract void registerPositionUpdateListener(IPositionUpdateListener listener);
	public abstract void unregisterPositionUpdateListener(IPositionUpdateListener listener);
}
