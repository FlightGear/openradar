package de.knewcleus.openradar.view.map;

import de.knewcleus.openradar.view.ICanvas;
import de.knewcleus.openradar.view.IUpdateManager;
import de.knewcleus.openradar.view.ViewerAdapter;

public class MapViewerAdapter extends ViewerAdapter implements IMapViewerAdapter {
	protected IProjection projection;
	
	public MapViewerAdapter(ICanvas canvas, IUpdateManager updateManager,
			IProjection projection) {
		super(canvas, updateManager);
		this.projection = projection;
	}

	@Override
	public IProjection getProjection() {
		return projection;
	}
	
	public void setProjection(IProjection projection) {
		this.projection = projection;
		notify(new ProjectionNotification(this));
	}
}
