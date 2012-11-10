package de.knewcleus.openradar.view.objects;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.xplane.Helipad;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class HelipadSymbol extends AViewObject {

//    private Helipad helipad;
    
    public HelipadSymbol(Helipad helipad, int minScale, int maxScale) {
        super(Palette.RUNWAY);
//        this.helipad = helipad;
        setMinScalePath(minScale);
        setMaxScalePath(maxScale);
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        
        path = new Path2D.Double();
        final double x, y;
        x = newDisplayPosition.getX();
        y = newDisplayPosition.getY();
        path.moveTo(x, y);
        path.append(new Ellipse2D.Double(x - 8.0d, y - 8.0d, 16.0d, 16.0d), false);
        path.moveTo(x - 3, y - 4);
        path.lineTo(x - 3, y + 4);
        path.moveTo(x - 3, y);
        path.lineTo(x + 3, y);
        path.moveTo(x + 3, y - 4);
        path.lineTo(x + 3, y + 4);
    }
}
