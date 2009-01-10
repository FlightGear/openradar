package de.knewcleus.openradar.rpvd;

import de.knewcleus.openradar.notify.Notifier;
import de.knewcleus.openradar.tracks.ITrack;

public class TrackDisplayState extends Notifier {
	protected final ITrack track;
	protected boolean selected = false;

	public TrackDisplayState(ITrack track) {
		this.track = track;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		if (this.selected == selected) {
			return;
		}
		this.selected = selected;
		notify(new SelectionChangeNotification(this));
	}
	
	public ITrack getTrack() {
		return track;
	}
}
