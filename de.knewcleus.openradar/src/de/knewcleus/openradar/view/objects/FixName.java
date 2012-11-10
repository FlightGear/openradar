package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class FixName extends AViewObject {

    public FixName(Intersection fix, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, fix.getIdentification(), minScaleText, maxScaleText);
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX()+12,newDisplayPosition.getY()));
    }
}
