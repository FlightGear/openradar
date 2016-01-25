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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.groundnet;

import java.awt.geom.Point2D;

import de.knewcleus.openradar.gui.status.runways.GuiRunway;


public class MapRunway implements ITaxiWayObject{

    private final String name;
    private GuiRunway rw;
    private Point2D geoPoint;  
    private double runwayLengthFt;
    private double runwayWidthFt;
    private float trueHeading;
    
    public MapRunway(GuiRunway runway) {
        rw = runway;
        name = runway.getCode();
        geoPoint=rw.getRunwayEnd().getGeographicPosition();
        runwayLengthFt = rw.getLengthFt();
        runwayWidthFt = rw.getWidthFt();
        trueHeading = rw.getRunwayEnd().getTrueHeading();
    }

    public String getName() {
        return name;
    }

    public Point2D getGeoPoint() {
        return geoPoint;
    }

    public boolean isLandingActive() {
        return rw.isLandingActive();
    }

    public boolean isStartingActive() {
        return rw.isStartingActive();
    }

    public double getRunwayLengthFt() {
        return runwayLengthFt;
    }

    public double getRunwayWidthFt() {
        return runwayWidthFt;
    }

    public float getRunwayTrueHeading() {
        return trueHeading;
    }

}
