package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiPoint;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class HoldPoint extends AViewObject {

//    private TaxiWaySegment seg;
    private TaxiPoint taxiPoint;
    private double radius;
    
    public HoldPoint(TaxiWaySegment seg, TaxiPoint taxiPoint, int minScalePath, int maxScalePath) {
        super(Color.white);
//      this.seg=seg;
        this.taxiPoint = taxiPoint;
        fillPath=true;
        this.minScalePath = minScalePath;
        this.maxScalePath = maxScalePath;
        if(taxiPoint instanceof ParkPos) {
            color = new Color(177,181,64);
            radius = 2;
        } else {
            color = Color.white;
            radius = 2;
        }
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {

        Point2D logPoint = mapViewAdapter.getProjection().toLogical(taxiPoint.getGeoPoint2D());
        Point2D point = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint,null);

        path = new Path2D.Double();
        path.append(new Ellipse2D.Double(point.getX()-radius, point.getY()-radius, 2*radius, 2*radius), false);

    }
}