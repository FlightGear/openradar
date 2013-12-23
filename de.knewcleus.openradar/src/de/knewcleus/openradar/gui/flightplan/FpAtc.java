package de.knewcleus.openradar.gui.flightplan;

import java.awt.geom.Point2D;

public class FpAtc {

    public final String callSign;
    public final String frequency;
    public final Point2D geoPosition;
    public final double distance;
    
    public FpAtc(String callSign, String frequency, Point2D geoPosition, double distance) {
        this.callSign=callSign;
        this.frequency=frequency;
        this.geoPosition=geoPosition;
        this.distance=distance;
    }
}
