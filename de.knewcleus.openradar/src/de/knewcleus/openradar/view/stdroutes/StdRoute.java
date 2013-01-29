/**
 * Copyright (C) 2013 Wolfram Wagner 
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
package de.knewcleus.openradar.view.stdroutes;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.gui.setup.AirportData;

public class StdRoute {

    private final String name;
    private final AirportData data;
    private String activeLandingRunways=null;
    private String activeStartingRunways=null;
    
    private final List<AStdRouteElement> elements = new ArrayList<AStdRouteElement>();
    
    public StdRoute(AirportData data, String name) {
        this.name = name;
        this.data = data;
    }
    
    public String getActiveLandingRunways() {
        return activeLandingRunways;
    }

    public void setActiveLandingRunways(String activeLandingRunways) {
        this.activeLandingRunways = activeLandingRunways;
    }

    public String getActiveStartingRunways() {
        return activeStartingRunways;
    }

    public void setActiveStartingRunways(String activeStartingRunways) {
        this.activeStartingRunways = activeStartingRunways;
    }

    public void addSegment(AStdRouteElement e) {
        elements.add(e);
    }
 
    public String getName() {
        return name;
    }
    
    public List<AStdRouteElement> getElements() {
        return elements;
    }

    public Point2D getPoint(String pointDescr, AStdRouteElement previous) {
        if(pointDescr.contains(",")) {
            String lat = pointDescr.substring(0,pointDescr.indexOf(","));
            String lon = pointDescr.substring(pointDescr.indexOf(",")+1);
            return new Point2D.Double(Double.parseDouble(lon), Double.parseDouble(lat));
        } else if ("last".equalsIgnoreCase(pointDescr)) {
            if(previous==null) {
                throw new IllegalArgumentException("Point is referenced as \"last\" although there is no previous segment!");
            }
            return previous.getEndPoint();
        } else {
            if(data.getNavaidDB().getNavaid(pointDescr)==null) {
                throw new IllegalArgumentException("Navaid "+pointDescr+" not found!");
            }
            return data.getNavaidDB().getNavaid(pointDescr).getGeographicPosition();
        }
    }
}
