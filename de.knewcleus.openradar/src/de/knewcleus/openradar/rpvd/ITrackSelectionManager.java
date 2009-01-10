package de.knewcleus.openradar.rpvd;

public interface ITrackSelectionManager {

	public abstract TrackDisplayState getSelectedTrack();

	public abstract void selectTrack(TrackDisplayState track);

	public abstract void deselect();

}