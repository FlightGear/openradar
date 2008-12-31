package de.knewcleus.openradar.tracks;

import de.knewcleus.openradar.notify.INotification;

/**
 * A TrackUpdateNotification is issued by a track when the track is updated
 * with new state information.
 * 
 * @author Ralf Gerlich
 *
 */
public class TrackUpdateNotification implements INotification {
	protected final ITrack source;

	public TrackUpdateNotification(ITrack source) {
		this.source = source;
	}

	@Override
	public ITrack getSource() {
		return source;
	}

}
