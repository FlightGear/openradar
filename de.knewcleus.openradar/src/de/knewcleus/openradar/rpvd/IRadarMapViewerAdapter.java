package de.knewcleus.openradar.rpvd;

import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public interface IRadarMapViewerAdapter extends IMapViewerAdapter {
	/**
	 * @return the track history length.
	 */
	public int getTrackHistoryLength();

	/**
	 * Set the track history length.
	 */
	public abstract void setTrackHistoryLength(int trackHistoryLength);

	/**
	 * @return the lookahead-time represented by the heading vector.
	 */
	public abstract void setHeadingVectorTime(double headingVectorTime);

	/**
	 * Set the lookahead-time represented by the heading vector.
	 */
	public abstract double getHeadingVectorTime();
}
