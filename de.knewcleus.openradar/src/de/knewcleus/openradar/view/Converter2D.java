package de.knewcleus.openradar.view;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public abstract class Converter2D {

    /**
     * This method returns a point in a direction and a distance. It assumes an coordinate system with origin in the top left, and y increasing downwards,
     * like our screen coordinates and a heading expressed in relation to our north, growing clockwise. 
     * 
     * @param origin
     * @param heading
     * @param distance
     * @return
     */
    public static Point2D getMapDisplayPoint(Point2D origin, double heading, double distance) {
        double headingCorrected = (heading-90d)*2*Math.PI/360d ;
        
        double x = distance * Math.cos(headingCorrected);
        double y = distance * Math.sin(headingCorrected);

        return addPoints(origin, new Point2D.Double(x,y));
    }
    
    public Point2D getGeographicPoint(Point2D origin, double heading, double distance) {
        return null;
    }

    public static Point2D addPoints(Point2D point1, Point2D point2) {
        return new Point2D.Double(point1.getX()+point2.getX(),point1.getY()+point2.getY());
    }
    
    
    public static double getFeetToDots(double distance, IMapViewerAdapter mapViewerAdapter) {
        double scale = mapViewerAdapter.getLogicalScale();
        scale = scale == 0 ? 1 : scale;
        return distance / 6076d / scale * 1900d; // the last number is the global correction factor for distances
    }
    
    public double getDistanceMiles(Point2D geoPoint1, Point2D geoPoint2) {
        return 0d;
    }

    public static double getMilesPerDot(IMapViewerAdapter mapViewerAdapter) {
        return 1d/getFeetToDots(Units.NM/Units.FT, mapViewerAdapter);
    }

    public static double normalizeAngle(double d) {
        while(d>=360) {
            d = d-360;
        }
        while(d<0) {
            d = d+360;
        }
        return d;
    }

}
