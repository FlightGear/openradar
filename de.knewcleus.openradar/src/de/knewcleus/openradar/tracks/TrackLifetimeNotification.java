package de.knewcleus.openradar.tracks;

import de.knewcleus.openradar.notify.INotification;

/**
 * A TrackLifetimeNotification is issued by a track manager whenever
 * a new track is created or a lost track is retired.
 * 
 * @see ITrackManager
 * @see ITrack
 * 
 * @author Ralf Gerlich
 *
 */
public class TrackLifetimeNotification implements INotification {
	protected final ITrackManager trackManager;
	protected final ITrack newTrack;
	protected final LifetimeState lifetimeState;
	
	public enum LifetimeState {
		CREATED, RETIRED;
	}
	
	public TrackLifetimeNotification(ITrackManager trackManager,
			ITrack track,
			LifetimeState lifetimeState) {
		this.trackManager = trackManager;
		this.newTrack = track;
		this.lifetimeState = lifetimeState;
	}	

	@Override
	public ITrackManager getSource() {
		return trackManager;
	}

	public ITrack getTrack() {
		return newTrack;
	}
	
	public LifetimeState getLifetimeState() {
		return lifetimeState;
	}
}
