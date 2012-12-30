package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class ParkingPosition extends AViewObject {

    private AirportData data;
    private String activeText;
    private ParkPos parkPos;

    public ParkingPosition(AirportData data, TaxiWaySegment seg, ParkPos parkPos, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, parkPos.getNumber().isEmpty() ? parkPos.getName() : parkPos.getNumber(), minScaleText, maxScaleText);
        this.data = data;
        this.activeText = parkPos.getNumber().isEmpty() ? parkPos.getName() : parkPos.getNumber();
        this.parkPos = parkPos;
        
        this.fillPath=true;
//        // try to hide even numbers at a lower level to reduce overlap
//        try {
//            int i = Integer.parseInt(text);
//            if (i % 2 == 0) {
//                this.maxScaleText = 8;
//            }
//        } catch (NumberFormatException e) {}
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {

        Point2D logPoint = mapViewAdapter.getProjection().toLogical(parkPos.getGeoPoint2D());
        Point2D point = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint, null);

        double x = point.getX()-5;
        double y = point.getY()+5;
        
        setTextCoordinates(new Point2D.Double(x, y));
        if(data.getRadarObjectFilterState("PPN")) {
            text = activeText;
        } else {
            text = null;
        }
        
    }
}
