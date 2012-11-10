package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class IlsMarkerBeacon extends AViewObject {

    private MarkerBeacon mb;

    public IlsMarkerBeacon(MarkerBeacon mb) {
        super(Palette.GLIDESLOPE);
        this.stroke = new BasicStroke(0.3f);
        this.mb=mb;
        switch(mb.getType()) {
        case Inner:
            color = Color.white;
            break;
        case Middle:
            color = Color.orange;
            break;
        case Outer:
            color = Palette.GLIDESLOPE;
            break;
        }

    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewerAdapter) {
        if (mb.getRunwayEnd()==null ||  !mb.getRunwayEnd().isLandingActive() || mb.getRunwayEnd().getGlideslope()==null){
            path=null;
            return;
        }
        
        float reverseHeading = mb.getRunwayEnd().getTrueHeading()+180;
        
        
        Point2D startPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading-90, 700d / mapViewerAdapter.getLogicalScale());
        Point2D endPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading+90, 700d / mapViewerAdapter.getLogicalScale());

        Point2D controlPoint1 = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, 200d / mapViewerAdapter.getLogicalScale());
        Point2D controlPoint2 = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading+180, 200d / mapViewerAdapter.getLogicalScale());
        
        path = new Path2D.Double();
        path.append(new QuadCurve2D.Double(startPoint.getX(),startPoint.getY(),
                                           controlPoint1.getX(),controlPoint1.getY(),
                                           endPoint.getX(),endPoint.getY()),false);
        path.append(new QuadCurve2D.Double(startPoint.getX(),startPoint.getY(),
                controlPoint2.getX(),controlPoint2.getY(),
                endPoint.getX(),endPoint.getY()),false);
    }
}
