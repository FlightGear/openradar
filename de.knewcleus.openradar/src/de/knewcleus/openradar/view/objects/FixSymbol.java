package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class FixSymbol extends AViewObject {

    private AirportData data;
    
    public FixSymbol(AirportData data, int minScale, int maxScale) {
        super(Color.lightGray);
        this.data = data;
        setMinScalePath(minScale);
        setMaxScalePath(maxScale);
    }

    @Override
    protected void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        
        path = new Path2D.Double();
        
        if(data.getRadarObjectFilterState("FIX")) {
            final double x, y;
            x = newDisplayPosition.getX();
            y = newDisplayPosition.getY();
            path.moveTo(x -3 , y + 3 );
            path.lineTo(x, y);
            path.lineTo(x + 3, y + 3);
            path.lineTo(x -3 , y + 3);
        }        
    }

}
