package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class FixSymbol extends AViewObject {

    public FixSymbol(int minScale, int maxScale) {
        super(Color.lightGray);
        setMinScalePath(minScale);
        setMaxScalePath(maxScale);
    }

    @Override
    protected void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        
        path = new Path2D.Double();
        final double x, y;
        x = newDisplayPosition.getX();
        y = newDisplayPosition.getY();
        path.moveTo(x -3 , y + 3 );
        path.lineTo(x, y);
        path.lineTo(x + 3, y + 3);
        path.lineTo(x -3 , y + 3);
        
    }

}
