/**
 * Copyright (C) 2013,2015 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.openradar.gui.GuiMasterController;
import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.status.runways.GuiRunway;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class StdRoute {

    public enum DisplayMode {
        always, optional, star, sid, runway
    }

    private final IMapViewerAdapter mapViewerAdapter;

    private final String name;
    private DisplayMode displayMode;
    private final AirportData data;
    private final float zoomMin;
    private final float zoomMax;

    private HashSet<String> activeLandingRunways = new HashSet<>();
    private HashSet<String> activeStartingRunways = new HashSet<>();

    // private volatile boolean selected = false;
    // private final Set<String> navaids = new HashSet<String>();
    // private Color navaidColor = null;

    private final Set<NavaidList> navaids = new HashSet<NavaidList>();

    private final List<AStdRouteElement> elements = new ArrayList<AStdRouteElement>();
    /** The list of routes, that use this route as an include */
    private final List<StdRoute> parentRoutes = Collections.synchronizedList(new ArrayList<StdRoute>());

    private final Logger log = Logger.getLogger(StdRoute.class);

    public StdRoute(AirportData data, IMapViewerAdapter mapViewerAdapter, String name, String displayMode, String zoomMin, String zoomMax) {
        this.mapViewerAdapter = mapViewerAdapter;

        this.name = name;

        try {
            this.displayMode = DisplayMode.valueOf(displayMode);
        } catch (Exception e) {
            if (displayMode != null) {
                Logger.getLogger(this.getClass()).info("Unrecognized display mode " + displayMode);
            }
            this.displayMode = DisplayMode.runway;
        }
        this.data = data;
        this.zoomMin = zoomMin != null ? Float.parseFloat(zoomMin) : 0;
        this.zoomMax = zoomMax != null ? Float.parseFloat(zoomMax) : Integer.MAX_VALUE;

    }

    // public synchronized boolean isSelected() {
    // return selected;
    // }

    // public synchronized void setSelected(boolean selected) {
    // this.selected = selected;
    // }

    public String getName() {
        return name;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public AirportData getAirportData() {
        return data;
    }

    public Set<String> getActiveLandingRunways() {
        return activeLandingRunways;
    }

    public void setActiveLandingRunways(String cs) {
        String[] array = cs.split(",");
        this.activeLandingRunways.addAll(Arrays.asList(array));
    }

    public Set<String> getActiveStartingRunways() {
        return activeStartingRunways;
    }

    public void setActiveStartingRunways(String cs) {
        String[] array = cs.split(",");
        this.activeStartingRunways.addAll(Arrays.asList(array));
    }

    public synchronized boolean contains(Point p) {
        for (AStdRouteElement e : elements) {
            if (e.contains(p)) {
                return true;
            }
        }
        return false;
    }

    public synchronized boolean containsNavaid(IIntersection navPoint) {
        for (NavaidList nl : navaids) {
            if (nl.containsNavaid(navPoint)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void setNavaids(String navaids, StdRouteAttributes attributes) {
        NavaidList nlist = new NavaidList(attributes);
        this.navaids.add(nlist);
        StringTokenizer st = new StringTokenizer(navaids, ",");
        while (st.hasMoreElements()) {
            nlist.addNavaid(st.nextToken().toUpperCase());
        }
    }

    public synchronized Color getNavaidColor(IIntersection navPoint) {

        for (NavaidList nList : navaids) {
            if (nList.containsNavaid(navPoint)) {
                return nList.getAttributes().getColor();
            }
        }
        return Color.gray;
    }

    public boolean isVisible(GuiMasterController master) {
        if(isParentRouteVisible(master)) {
            // this means, this route is included into another route which is visible.
            return true;
        }
        AirportData data = master.getAirportData();

        if (mapViewerAdapter.getLogicalScale() < zoomMin || mapViewerAdapter.getLogicalScale() > zoomMax) {
            return false;
        }

        GuiRadarContact c = master.getRadarContactManager().getSelectedContact();
        if(c!=null) {
            String assignedRoute = c.getFlightPlan().getAssignedRoute();
            if(assignedRoute!=null) {
                if(getName().equals(assignedRoute)) {
                    return true;
                }
            }
        }
        if(displayMode.equals(DisplayMode.always)
           || ( displayMode.equals(DisplayMode.optional) && data.getRadarObjectFilterState("STARSID")==true)) {
            return true;
        }

        // display mode runway
  
        // SPEED optimization: Get the list from data and remove all runways that are not in the route definitions
        if(!(activeStartingRunways.isEmpty() && activeLandingRunways.isEmpty())) { 
            if ( data.isActiveRouteRunwayContained(activeStartingRunways,activeLandingRunways) ) {
                return true;
            }
        }

        // main switch off
        if(data.getRadarObjectFilterState("STARSID")!=true) {
            return false;
        }

        // no runways defined + main switch is on
        return false;
    }

    private boolean isParentRouteVisible(GuiMasterController master) {
        for (StdRoute parentRoute : parentRoutes) {
            if (parentRoute.isVisible(master)) {
                return true;
            }
        }
        return false;
    }

    public void addElement(AStdRouteElement e) {
        elements.add(e);
    }

    public List<AStdRouteElement> getElements() {
        return elements;
    }

    public Point2D getPoint(String pointDescr, AStdRouteElement previous) {
        return getPoint(data, mapViewerAdapter, pointDescr, previous);
    }

    public static Point2D getPoint(AirportData data, IMapViewerAdapter mapViewerAdapter, String pointDescr, AStdRouteElement previous) {

        if (pointDescr.matches("[A-Z0-9]*-RW[A-Z0-9]*")) {
            String airportCode = pointDescr.substring(0, pointDescr.indexOf("-"));
            if (!data.getAirportCode().equals(airportCode)) {
                throw new IllegalArgumentException("Wrong airport referenced in " + pointDescr + "!");
            }

            String runwayCode = pointDescr.substring(pointDescr.indexOf("-") + 1 + 2); // +2 is for the prefix 'RW' in
                                                                                       // front of the number
            GuiRunway rw = data.getRunways().get(runwayCode);
            if (rw == null) {
                throw new IllegalArgumentException("Runway end for definition " + pointDescr + " not found!");
            }
            Point2D point = rw.getRunwayEnd().getOppositeEnd().getGeographicPosition();
            return point;
        }
        if (pointDescr.contains("@")) {
            if (!pointDescr.contains("NM"))
                throw new IllegalArgumentException("Wrong point definition: " + pointDescr);
            int pos = pointDescr.indexOf("NM");
            float distance = Float.parseFloat(pointDescr.substring(0, pos));
            int pos2 = pointDescr.indexOf("@");
            float angle = Float.parseFloat(pointDescr.substring(pos + 2, pos2)) + (float) data.getMagneticDeclination();
            String id = pointDescr.substring(pos2 + 1);

            Point2D navaidPoint = null;
            if (data.getNavaidDB().getNavaid(id) != null) {
                // search the referenced navaid
                navaidPoint = data.getNavaidDB().getNavaid(id).getGeographicPosition();
            }
            if (navaidPoint == null) {
                // another recursive redirection
                navaidPoint = getPoint(data, mapViewerAdapter, id, previous);
                if (navaidPoint == null) {
                    throw new IllegalArgumentException("Navaid " + pointDescr + " not found!");
                }
            }
            Point2D point = new IndirectPoint2D(mapViewerAdapter, navaidPoint, angle, distance);
            return point;
        } else if (pointDescr.contains(",")) {
            Point2D geoPoint = parsePoint(pointDescr);
            return geoPoint;
            // } else if ("last".equalsIgnoreCase(pointDescr)) {
            // if(previous==null) {
            // throw new
            // IllegalArgumentException("Point is referenced as \"last\" although there is no previous segment!");
            // }
            // return previous.getEndPoint();
        } else {
            if (data.getNavaidDB().getNavaid(pointDescr) == null) {
                throw new IllegalArgumentException("Navaid " + pointDescr + " not found!");
            }
            return data.getNavaidDB().getNavaid(pointDescr).getGeographicPosition();
        }
    }

    /**
     * This method can read the point coordinates ("lat,lon") directly as decimals as well as in degree, minute and
     * seconds format.
     *
     * @param pointDescr
     * @return
     */
    private static Point2D parsePoint(String pointDescr) {
        String lat = pointDescr.substring(0, pointDescr.indexOf(","));
        double latD = 0;
        if (lat.contains("°") && lat.contains("'") && lat.contains("''")) {
            // N47°54'26
            int sign = lat.substring(0, 1).equalsIgnoreCase("N") ? 1 : -1;
            Double degrees = Double.parseDouble(lat.substring(1, lat.indexOf("°")).trim());
            Double minutes = Double.parseDouble(lat.substring(lat.indexOf("°") + 1, lat.indexOf("'")).trim());
            Double seconds = Double.parseDouble(lat.substring(lat.indexOf("'") + 1, lat.indexOf("''")).trim());
            latD = (degrees + minutes / 60d + seconds / 3600d) * sign;

        } else if (lat.contains("°") && lat.contains("'")) {
            // N47°54.26
            int sign = lat.substring(0, 1).equalsIgnoreCase("N") ? 1 : -1;
            Double degrees = Double.parseDouble(lat.substring(1, lat.indexOf("°")).trim());
            Double minutes = Double.parseDouble(lat.substring(lat.indexOf("°") + 1, lat.indexOf("'")).trim());
            latD = (degrees + minutes / 60) * sign;

        } else {
            latD = Double.parseDouble(lat);
        }

        String lon = pointDescr.substring(pointDescr.indexOf(",") + 1);
        double lonD = 0;
        if (lon.contains("°") && lon.contains("'") && lon.contains("''")) {
            // E7°54'15
            int sign = lon.substring(0, 1).equalsIgnoreCase("E") ? 1 : -1;
            Double degrees = Double.parseDouble(lon.substring(1, lon.indexOf("°")).trim());
            Double minutes = Double.parseDouble(lon.substring(lon.indexOf("°") + 1, lon.indexOf("'")).trim());
            Double seconds = Double.parseDouble(lon.substring(lon.indexOf("'") + 1, lon.indexOf("''")).trim());

            lonD = (degrees + minutes / 60d + seconds / 3600d) * sign;

        } else if (lon.contains("°") && lon.contains("'")) {
            // E7°54.15
            int sign = lon.substring(0, 1).equalsIgnoreCase("E") ? 1 : -1;
            Double degrees = Double.parseDouble(lon.substring(1, lon.indexOf("°")).trim());
            Double minutes = Double.parseDouble(lon.substring(lon.indexOf("°") + 1, lon.indexOf("'")).trim());

            lonD = (degrees + minutes / 60) * sign;
        } else {
            lonD = Double.parseDouble(lon);
        }

        Point2D geoPoint = new Point2D.Double(lonD, latD);
        return geoPoint;
    }

    public int getSize() {
        return elements.size();
    }

    public void includeRoute(List<StdRoute> stdRoutes, String routeName) {
        StdRoute routeToInclude = null;
        for (StdRoute r : stdRoutes) {
            if (r.getName().equals(routeName)) {
                routeToInclude = r;
            }
        }
        if (routeToInclude != null) {
            routeToInclude.registerParentRoute(this);
        } else {
            log.warn(getName() + ": Could not find route '" + routeName + "' to include it.");
        }
    }

    private void registerParentRoute(StdRoute stdRoute) {
        parentRoutes.add(stdRoute);
    }

    /**
     * checks if this route is assigned to the selected contact or if it is implicitelly assigned, because it is
     * included into an assigned route.
     * 
     * @param master
     * @return true if assigned
     */
    public boolean isRouteAssigned(GuiMasterController master) {
        boolean result = master.getRadarContactManager().isRouteAssigned(getName());
        if (result == false) {
            for (StdRoute parentRoute : parentRoutes) {
                if (parentRoute.isRouteAssigned(master)) {
                    return true;
                }
            }
        }
        return result;
    }
}
