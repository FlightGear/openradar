package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class TaxiwayLine extends AViewObject {
    
    private TaxiWaySegment seg;

    public TaxiwayLine(TaxiWaySegment seg,int minScale, int maxScale) {
        super(new Color(177,181,64));
        setMinScalePath(minScale);
        setMaxScalePath(maxScale);
//        if(seg.getBegin().getPaintStyle()==1) {
//            // center line
//            color = Color.yellow;
//        }
//        if(seg.getBegin().getPaintStyle()==4) {
//            // hold position
//            color = Color.white;
//        }
//        if(seg.getBegin().getPaintStyle()==5) {
//            // hold Position
//            color = Color.blue;
//        }
//        if(seg.getBegin().getPaintStyle()==6) {
//            // ILS hold
//            color = Color.red;
//        }
//        if(seg.getBegin().getPaintStyle()==7) {
//            // taxi center line
//            color = Color.yellow;
//        }

        
        this.stroke = new BasicStroke(0.3f);
        this.seg = seg;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewerAdapter) {

        boolean isBeginPP = seg.getBegin() instanceof ParkPos;
        boolean isEndPP = seg.getEnd() instanceof ParkPos;
        
        Point2D logStartPoint = mapViewerAdapter.getProjection().toLogical(seg.getBegin().getGeoPoint2D());
        Point2D startPoint = mapViewerAdapter.getLogicalToDeviceTransform().transform(logStartPoint,null);
        Point2D logEndPoint = mapViewerAdapter.getProjection().toLogical(seg.getEnd().getGeoPoint2D());
        Point2D endPoint = mapViewerAdapter.getLogicalToDeviceTransform().transform(logEndPoint,null);
        
        if(isBeginPP) {
            startPoint = new Point2D.Double(startPoint.getX()-8*seg.getOrientationX(),startPoint.getY()-8*seg.getOrientationY());
        }
        if(isEndPP) {
            endPoint = new Point2D.Double(endPoint.getX()-8*seg.getOrientationX(),endPoint.getY()-8*seg.getOrientationY());
        }
        
        path = new Path2D.Double();
        path.append(new Line2D.Double(startPoint, endPoint), false);
    }

}
