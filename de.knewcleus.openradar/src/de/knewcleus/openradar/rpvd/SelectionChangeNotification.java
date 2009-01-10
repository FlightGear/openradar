package de.knewcleus.openradar.rpvd;

import de.knewcleus.openradar.notify.INotification;
import de.knewcleus.openradar.notify.INotifier;

public class SelectionChangeNotification implements INotification {
	protected final TrackDisplayState source;
	
	public SelectionChangeNotification(TrackDisplayState source) {
		this.source = source;
	}

	@Override
	public INotifier getSource() {
		return source;
	}

}
