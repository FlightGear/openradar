package de.knewcleus.openradar.view;

import java.awt.geom.Point2D;

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
        return distance / 5280d / scale *3600d;
    }
    
    public double getDistanceMiles(Point2D geoPoint1, Point2D geoPoint2) {
        return 0d;
    }

}
