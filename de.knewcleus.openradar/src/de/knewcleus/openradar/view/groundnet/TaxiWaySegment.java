package de.knewcleus.openradar.view.groundnet;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.Vector2D;


public class TaxiWaySegment implements TaxiWayObjext{

    private String name;
    private TaxiPoint begin;
    private TaxiPoint end;
    private boolean isPushBackRoute;
    private double dx = 0;
    private double dy = 0;

    
    public TaxiWaySegment(String name, TaxiPoint begin, TaxiPoint end, boolean isPushBackRoute) {
        this.name=name;
        this.begin=begin;
        this.end=end;
        this.isPushBackRoute=isPushBackRoute;
        
        // calculate the normed directions for placing of texts
        
        if(begin instanceof ParkPos) {
            dx = begin.getLon()-end.getLon();
            dy = begin.getLat()-end.getLat();
        } 
        if(end instanceof ParkPos) {
            dx = end.getLon()-begin.getLon();
            dy = end.getLat()-begin.getLat();
        }
        Vector2D v2d = new Vector2D(dx, dy);
        dx = dx/v2d.getLength();
        dy = -1* dy/v2d.getLength(); // because Y coordinates on screen point into other direction
    }

    public String getName() {
        return name;
    }

    public TaxiPoint getBegin() {
        return begin;
    }

    public TaxiPoint getEnd() {
        return end;
    }

    public boolean isPushBackRoute() {
        return isPushBackRoute;
    }

    public Point2D getGeoPoint() {
        return begin.getGeoPoint2D();
    }
    public double getOrientationX() {
        return dx;
    }

    public double getOrientationY() {
        return dy;
    }

}
