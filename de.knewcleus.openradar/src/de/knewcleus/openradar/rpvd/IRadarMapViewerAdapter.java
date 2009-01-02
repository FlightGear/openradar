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

	public abstract void setHeadingVectorTime(double headingVectorTime);

	public abstract double getHeadingVectorTime();
}
