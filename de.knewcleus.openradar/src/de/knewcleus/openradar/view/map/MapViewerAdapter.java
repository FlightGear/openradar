package de.knewcleus.openradar.view.map;

import java.awt.Point;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.ICanvas;
import de.knewcleus.openradar.view.IUpdateManager;
import de.knewcleus.openradar.view.ViewerAdapter;

public class MapViewerAdapter extends ViewerAdapter implements IMapViewerAdapter {
	protected IProjection projection;
	protected Point2D originalCenter = null;
	protected Point2D currentCenter = null;

	
	public MapViewerAdapter(ICanvas canvas, IUpdateManager updateManager, IProjection projection, Point2D center) {
		super(canvas, updateManager);
		this.projection = projection;
        this.currentCenter = center; 
        this.originalCenter = center;
	}

	@Override
	public IProjection getProjection() {
		return projection;
	}
	
	public void setProjection(IProjection projection) {
		this.projection = projection;
		notify(new ProjectionNotification(this));
	}

	public Point2D getGeoLocationOf(Point awtPoint) {
        Point2D result = new Point2D.Double();
        getDeviceToLogicalTransform().transform(new Point2D.Double(awtPoint.getX(),awtPoint.getY()), result);
        result = getProjection().toGeographical(result);
        return result;
    }

    public void shiftMap(double dx, double dy) {
        currentCenter = new Point2D.Double(currentCenter.getX()+dx,currentCenter.getY()+dy);
        setProjection(new LocalSphericalProjection(currentCenter));
    }       


    public void centerMap() {
        setProjection(new LocalSphericalProjection(originalCenter));
        currentCenter = originalCenter;
    }

	
}
