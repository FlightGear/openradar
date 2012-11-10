package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.xplane.Helipad;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class HelipadNumber extends AViewObject {

//    private Helipad helipad;
    
    public HelipadNumber(Helipad helipad, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, helipad.getDesignation(), minScaleText, maxScaleText);
//        this.helipad = helipad;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()-10,newDisplayPosition.getY()+10));
    }
}
