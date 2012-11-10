package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.RunwayEnd;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayEndIlsCone extends AViewObject {

    private RunwayEnd runwayEnd;

    public RunwayEndIlsCone(RunwayEnd runwayEnd) {
        super(Palette.GLIDESLOPE);
        this.stroke = new BasicStroke(0.3f);
        this.runwayEnd = runwayEnd;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewerAdapter) {
        if ( ! runwayEnd.isLandingActive() || runwayEnd.getGlideslope()==null){
            path=null;
            return;
        }
        
        float reverseHeading = runwayEnd.getTrueHeading()+180;
        
        // default values 18 SM +/- 10 degrees
        
        double length = Converter2D.getFeetToDots(runwayEnd.getGlideslope().getRange(), mapViewerAdapter); // feet!
        
        Point2D minorEndPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading-5, length);
        Point2D middleEndPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, length*0.9);
        Point2D majorEndPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading+5, length);
        
        path = new Path2D.Double();
        path.append(new Line2D.Double(newDisplayPosition, minorEndPoint), false);
        path.append(new Line2D.Double(minorEndPoint, middleEndPoint), true);
        path.append(new Line2D.Double(middleEndPoint, newDisplayPosition), true);
        path.append(new Line2D.Double(newDisplayPosition, majorEndPoint), true);
        path.append(new Line2D.Double(majorEndPoint, middleEndPoint), true);
    }
}
