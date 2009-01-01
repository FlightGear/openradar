package de.knewcleus.openradar.map;

public class MapViewAdapter extends ViewAdapter implements IMapViewAdapter {
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
