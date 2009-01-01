package de.knewcleus.openradar.map;

import de.knewcleus.openradar.notify.INotification;

/**
 * A projection notification is issued by a map view adapter whenever the projection changes.
 * 
 * @author Ralf Gerlich
 *
 */
public class ProjectionNotification implements INotification {
	protected final IMapViewAdapter source;

	public ProjectionNotification(IMapViewAdapter source) {
		this.source = source;
	}

	@Override
	public IMapViewAdapter getSource() {
		return source;
	}

}
