package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.fgfs.navdata.model.IMarkerBeacon.Type;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class MarkerBeaconName extends AViewObject {

//    private MarkerBeacon mb = null;
    
    public MarkerBeaconName(MarkerBeacon mb, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, (mb.getType()==Type.Inner?"IB":(mb.getType()==Type.Middle?"MB":"OB")), minScaleText, maxScaleText);
//        this.mb=mb;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()-16,newDisplayPosition.getY()+50));
    }
}
