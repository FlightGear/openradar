package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayEndCenterLine extends AViewObject {
    
    private RunwayEnd runwayEnd;
//    private double lengthMiles;

    public RunwayEndCenterLine(RunwayEnd runwayEnd, double lengthMiles) {
        super(Color.blue);
        this.stroke = new BasicStroke(0.3f);
        this.runwayEnd = runwayEnd;
//        this.lengthMiles=lengthMiles;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewerAdapter) {
        if ( ! runwayEnd.isLandingActive() || runwayEnd.getGlideslope()!=null) {
            
            path=null;
            return;
        }
        
        float reverseHeading = runwayEnd.getTrueHeading()+180;
        
        double length = Converter2D.getFeetToDots(18000d, mapViewerAdapter); // todo
        Point2D endPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, length);
        
        path = new Path2D.Double();
        path.append(new Line2D.Double(newDisplayPosition, endPoint), false);
    }

}
