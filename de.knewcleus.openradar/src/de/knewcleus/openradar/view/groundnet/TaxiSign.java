package de.knewcleus.openradar.view.groundnet;

import java.awt.geom.Point2D;

public class TaxiSign implements TaxiWayObjext {

    private double lat;
    private double lon;
    private double heading;
    private int size;
    private String text;
    private String displayText;

    public TaxiSign(String lat, String lon, String heading, String size, String text) {
        this.lat = Double.parseDouble(lat);
        this.lon = Double.parseDouble(lon);
        this.heading = Double.parseDouble(heading);
        this.size = Integer.parseInt(size);
        this.text = text;
        displayText = text.replaceAll("\\{.+}}}", "");
        //displayText = displayText.replaceAll("\\{\\^[udlr]\\}", " ");
        displayText = displayText.replaceAll("\\{\\^[u]\\}", "^");
        displayText = displayText.replaceAll("\\{\\^[l]\\}", "<");
        displayText = displayText.replaceAll("\\{\\^[r]\\}", ">");
        displayText = displayText.replaceAll("\\{\\^l[ud]\\}", " ");
        displayText = displayText.replaceAll("\\{\\^r[ud]\\}", " ");
        displayText = displayText.replaceAll("\\{\\^r[123]\\}", " ");
        displayText = displayText.replaceAll("no-entry", " ");
        displayText = displayText.replaceAll("\\{@Y[123]?\\}", " ");
        displayText = displayText.replaceAll("\\{@R[123]?\\}", " ");
        displayText = displayText.replaceAll("\\{@L[123]?\\}", " ");
        displayText = displayText.replaceAll("\\{@B[45]?\\}", " ");
        displayText = displayText.replaceAll("[A-Z]\\d", "");
        displayText = displayText.replaceAll("\\d[RL]?", "");
        displayText = displayText.replaceAll("<\\w", "");
        displayText = displayText.replaceAll(">\\w", "");
        displayText = displayText.replaceAll("\\^\\w", "");
        displayText = displayText.replaceAll("\\w<", "");
        displayText = displayText.replaceAll("\\w>", "");
        displayText = displayText.replaceAll("\\w\\^", "");
        displayText = displayText.replaceAll("[<>^]", "");
        displayText = displayText.replaceAll("STOP", "");
        displayText = displayText.replaceAll("[-_|]", " ").trim();
        System.out.println ("Taxisign: "+text+" => "+displayText);
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getHeading() {
        return heading;
    }

    public int getSize() {
        return size;
    }

    public String getText() {
        return text;
    }

    public Point2D getGeoPoint() {
        return new Point2D.Double(lon,lat);
    }

    public String getTextForDisplay() {
        return displayText;
    }
}
