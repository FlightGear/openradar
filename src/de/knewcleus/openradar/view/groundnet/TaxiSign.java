/**
 * Copyright (C) 2012 Wolfram Wagner 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
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
        // System.out.println ("Taxisign: "+text+" => "+displayText);
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
