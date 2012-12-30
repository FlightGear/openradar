package de.knewcleus.openradar.view.groundnet;

import java.awt.geom.Point2D;

public class TaxiPoint {

    private int index;
    private double lat;
    private double lon;
    private double ctrlLat;
    private double ctrlLon;
    private boolean isOnRunway;
    private String holdPointType;
    private int currentPaintStyle=0;
    
    public TaxiPoint(String index, String lat, String lon, boolean isOnRunway, String holdPointType) {
        this.index = Integer.parseInt(index); 
        this.lat = parseLocation(lat);
        this.lon = parseLocation(lon);
        this.isOnRunway = isOnRunway;
        this.holdPointType = holdPointType;
    }

    public TaxiPoint(String code, String lat, String lon, String ctrlLat, String ctrlLon, int currentPaintStyle) {
        this.index = Integer.parseInt(code); 
        this.lat = Double.parseDouble(lat);
        this.lon = Double.parseDouble(lon);
        this.ctrlLat = Double.parseDouble(lat);
        this.ctrlLon = Double.parseDouble(lon);
        this.isOnRunway = false;
        this.holdPointType = "";
        
    }

    private double parseLocation(String location) {
        int sign = location.startsWith("S") || location.startsWith("W")?-1:1;
        int degree = Integer.parseInt(location.substring(1,location.indexOf(" ")));
        double minutes = Double.parseDouble(location.substring(location.indexOf(" ")));
        
        return (double)sign * degree + minutes/60d;
    }

    public int getIndex() {
        return index;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public boolean isOnRunway() {
        return isOnRunway;
    }

    public String getHoldPointType() {
        return holdPointType;
    }

    public Point2D getGeoPoint2D() {
        return new Point2D.Double(lon,lat);
    }

    public double getCtrlLat() {
        return ctrlLat;
    }

    public double getCtrlLon() {
        return ctrlLon;
    }

    public int getPaintStyle() {
        return currentPaintStyle;
    }

}
