package de.knewcleus.openradar.tracks;

import de.knewcleus.openradar.notify.INotification;

/**
 * A TrackLossNotification is issued by an ITrack whenever the loss
 * status of that track changes.
 * 
 * @author Ralf Gerlich
 * @see ITrack
 *
 */
public class TrackLossStatusNotification implements INotification {
	protected final ITrack track;

	public TrackLossStatusNotification(ITrack track) {
		this.track = track;
	}
	
	@Override
	public ITrack getSource() {
		return track;
	}
}
