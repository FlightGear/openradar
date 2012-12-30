package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.groundnet.TaxiSign;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

/**
 * This is a prototype which will not be used. It produces too much details. 
 * 
 * @author wolfram
 *
 */
public class TaxiSignObject extends AViewObject {

    TaxiSign sign;

    public TaxiSignObject(TaxiSign sign, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, sign.getTextForDisplay(), minScaleText, maxScaleText);
        this.sign = sign;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        int scale = (int) mapViewAdapter.getLogicalScale();
        scale = scale == 0 ? 1 : scale;
        scale = 15 * 10 / scale;
        if (scale < 10)
            scale = 10;
        if (scale > 15)
            scale = 15;

        setTextCoordinates(new Point2D.Double(newDisplayPosition.getX() + scale, newDisplayPosition.getY() + scale));
    }

//    @Override
//    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
//        double currentScale = mapViewAdapter.getLogicalScale();
//
//        if (text != null && minScaleText < currentScale && maxScaleText > currentScale) {
//            AffineTransform oldt = g2d.getTransform();
//
//            AffineTransform newt = new AffineTransform();
//            newt.setToRotation(Math.toRadians(sign.getHeading()+90), (float) textCoordinates.getX(), (float) textCoordinates.getY());
//            g2d.transform(newt);
//
//            if (font != null)
//                g2d.setFont(font);
//            g2d.drawString(text, (float) textCoordinates.getX(), (float) textCoordinates.getY());
//            g2d.transform(oldt);
//        }
//
//    }

}
