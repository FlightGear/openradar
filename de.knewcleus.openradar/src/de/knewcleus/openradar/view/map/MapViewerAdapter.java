package de.knewcleus.openradar.view.map;

import de.knewcleus.openradar.view.ViewerAdapter;

public class MapViewerAdapter extends ViewerAdapter implements IMapViewerAdapter {
	protected IProjection projection = new IdentityProjection();
	
	@Override
	public IProjection getProjection() {
		return projection;
	}
	
	public void setProjection(IProjection projection) {
		this.projection = projection;
		notify(new ProjectionNotification(this));
	}
}
