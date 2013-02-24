package de.knewcleus.openradar.view.stdroutes;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class IndirectPoint2D extends Point2D {

    private Point2D geoOrigPoint;
    private final double distance;
    private final double direction;
    private final IMapViewerAdapter viewerAdapter;
    
    public IndirectPoint2D(IMapViewerAdapter viewerAdapter, Point2D geoOrigPoint, double direction, double distance) {
        this.viewerAdapter=viewerAdapter;
        this.geoOrigPoint = geoOrigPoint;
        this.distance=distance;
        this.direction = direction;
    }

    @Override
    public double getX() {
        Point2D logPoint = viewerAdapter.getProjection().toLogical(geoOrigPoint);
        Point2D devicePoint = viewerAdapter.getLogicalToDeviceTransform().transform(logPoint, null);
        Point2D newDevPoint = Converter2D.getMapDisplayPoint(devicePoint, direction, Converter2D.getFeetToDots(distance * Units.NM /Units.FT, viewerAdapter));
        Point2D newLogPoint = viewerAdapter.getDeviceToLogicalTransform().transform(newDevPoint, null);
        return viewerAdapter.getProjection().toGeographical(newLogPoint).getX();
    }

    @Override
    public double getY() {
        Point2D logPoint = viewerAdapter.getProjection().toLogical(geoOrigPoint);
        Point2D devicePoint = viewerAdapter.getLogicalToDeviceTransform().transform(logPoint, null);
        Point2D newDevPoint = Converter2D.getMapDisplayPoint(devicePoint, direction, Converter2D.getFeetToDots(distance * Units.NM /Units.FT, viewerAdapter));
        Point2D newLogPoint = viewerAdapter.getDeviceToLogicalTransform().transform(newDevPoint, null);
        return viewerAdapter.getProjection().toGeographical(newLogPoint).getY();
    }

    @Override
    public void setLocation(double x, double y) {
        throw new IllegalStateException("This class is immutable");
    }
}
