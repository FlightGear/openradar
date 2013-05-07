package de.knewcleus.openradar.gui.setup;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navdata.model.IIntersection;

public class AdditionalFix implements IIntersection{

    private final String id;
    private final Point2D geoPos;
    private boolean hightlighted = false;

    public AdditionalFix(String id, String point) {
        this.id=id;
        try {
            double lon = Double.parseDouble(point.substring(0,point.indexOf(",")));
            double lat = Double.parseDouble(point.substring(point.indexOf(",")+1));
            geoPos = new Point2D.Double(lat, lon);
        } catch(Exception e) {
            throw new IllegalArgumentException("Could not parse point "+point, e);
        }
    }

    @Override
    public String getIdentification() {
        return id;
    }

    @Override
    public Point2D getGeographicPosition() {
        return geoPos;
    }

    @Override
    public synchronized boolean isHighlighted() {
        return hightlighted;
    }

    @Override
    public void setHighlighted(boolean highlighted) {
         this.hightlighted=highlighted;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof AdditionalFix) || obj ==null) return false;
        AdditionalFix otherOne = (AdditionalFix)obj;
        return this.id.equals(otherOne.id);
    }
}
