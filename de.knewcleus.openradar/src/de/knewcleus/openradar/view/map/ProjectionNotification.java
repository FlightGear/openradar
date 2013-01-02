package de.knewcleus.openradar.view.map;

import de.knewcleus.openradar.notify.INotification;

/**
 * A projection notification is issued by a map view adapter whenever the projection changes.
 * 
 * @author Ralf Gerlich
 *
 */
public class ProjectionNotification implements INotification {
	protected final IMapViewerAdapter source;

	public ProjectionNotification(IMapViewerAdapter source) {
		this.source = source;
	}

	@Override
	public IMapViewerAdapter getSource() {
		return source;
	}

}
