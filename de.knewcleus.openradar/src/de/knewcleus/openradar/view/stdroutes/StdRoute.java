/**
 * Copyright (C) 2013 Wolfram Wagner
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.sun.istack.internal.logging.Logger;

import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.status.runways.GuiRunway;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class StdRoute {

    public enum DisplayMode { always, optional, star, sid, runway }

    private final IMapViewerAdapter mapViewerAdapter;

    private final String name;
    private DisplayMode displayMode;
    private final AirportData data;
    private final float zoomMin;
    private final float zoomMax;
    protected final Stroke stroke;
    private final Color color;

    private String activeLandingRunways = null;
    private String activeStartingRunways = null;

//    private final Set<String> navaids = new HashSet<String>();
//    private Color navaidColor = null;

    private final Set<NavaidList> navaids = new HashSet<NavaidList>();

    private final List<AStdRouteElement> elements = new ArrayList<AStdRouteElement>();

    public StdRoute(AirportData data, IMapViewerAdapter mapViewerAdapter, String name, String displayMode, String zoomMin, String zoomMax, String stroke, String sLineWidth,
            String color) {
        this.mapViewerAdapter = mapViewerAdapter;

        this.name = name;

        try {
            this.displayMode = DisplayMode.valueOf(displayMode);
        } catch(Exception e) {
            if(displayMode!=null) {
                Logger.getLogger(this.getClass()).info("Unrecognized display mode "+displayMode);
            }
            this.displayMode = DisplayMode.runway;
        }
        this.data = data;
        this.zoomMin = zoomMin != null ? Float.parseFloat(zoomMin) : 0;
        this.zoomMax = zoomMax != null ? Float.parseFloat(zoomMax) : Integer.MAX_VALUE;

        Float lineWidth = sLineWidth != null ? Float.parseFloat(sLineWidth) : 2;

        if (stroke != null) {
            if (stroke.contains(",")) {
                // after the comma follows the linewith of the stroke, we need to parse and remove it
                int sep = stroke.indexOf(",");
                lineWidth = Float.parseFloat(stroke.substring(sep + 1));
                stroke = stroke.substring(0, sep);
            }
            if ("line".equalsIgnoreCase(stroke)) {
                this.stroke = new BasicStroke(lineWidth);

            } else if ("dashed".equalsIgnoreCase(stroke)) {
                float[] dashPattern = { 10, 10 };
                this.stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0);
            } else if ("dots".equalsIgnoreCase(stroke)) {
                float[] dashPattern = { lineWidth, 2 * lineWidth };
                this.stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, dashPattern, 0);
            } else if (stroke != null && stroke.contains("-")) {
                // this variant allows to define own patterns like 10-5-2-5

                StringTokenizer st = new StringTokenizer(stroke, "-");
                ArrayList<Float> pattern = new ArrayList<Float>();
                while (st.hasMoreElements()) {
                    pattern.add(Float.parseFloat(st.nextToken().trim()));
                }
                float[] patternArray = new float[pattern.size()];
                for (int i = 0; i < pattern.size(); i++) {
                    Float f = pattern.get(i);
                    patternArray[i] = (f != null ? f : 0);
                }
                this.stroke = new BasicStroke(lineWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, patternArray, 0);
            } else {
                this.stroke = new BasicStroke(lineWidth);
            }
        } else {
            this.stroke = new BasicStroke(lineWidth);
        }

        if (color != null) {
            StringTokenizer rgb = new StringTokenizer(color, ",");
            int r = Integer.parseInt(rgb.nextToken());
            int g = Integer.parseInt(rgb.nextToken());
            int b = Integer.parseInt(rgb.nextToken());
            this.color = new Color(r, g, b);
        } else {
            this.color = Color.gray;
        }
    }

    public String getName() {
        return name;
    }

    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public AirportData getAirportData() {
        return data;
    }

    public synchronized Stroke getStroke() {
        return stroke;
    }

    public synchronized Color getColor() {
        return color;
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

    public synchronized boolean containsNavaid(IIntersection navPoint) {
        for(NavaidList nl : navaids) {
            if(nl.containsNavaid(navPoint)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void setNavaids(String navaids, String navaidColor) {
        Color color;
        if (navaidColor != null) {
            StringTokenizer rgb = new StringTokenizer(navaidColor, ",");
            int r = Integer.parseInt(rgb.nextToken());
            int g = Integer.parseInt(rgb.nextToken());
            int b = Integer.parseInt(rgb.nextToken());
            color = new Color(r, g, b);
        } else {
            color = this.getColor();
        }
        NavaidList nlist = new NavaidList(color);
        this.navaids.add(nlist);
        StringTokenizer st = new StringTokenizer(navaids, ",");
        while (st.hasMoreElements()) {
            nlist.addNavaid(st.nextToken().toUpperCase());
        }
    }

    public synchronized Color getNavaidColor(IIntersection navPoint) {

        for(NavaidList nList : navaids) {
            if(nList.containsNavaid(navPoint)) {
                return nList.getColor();
            }
        }
        return getColor();
    }

    public boolean isVisible(AirportData data) {

        if (mapViewerAdapter.getLogicalScale() < zoomMin || mapViewerAdapter.getLogicalScale() > zoomMax) {
            return false;
        }


        if(displayMode.equals(DisplayMode.always)
           || ( displayMode.equals(DisplayMode.optional) && data.getRadarObjectFilterState("STARSID")==true)) {
            return true;
        }

        // display mode runway

        // main switch
        if(data.getRadarObjectFilterState("STARSID")!=true) {
            return false;
        }

        if (activeLandingRunways == null && activeStartingRunways == null) {
            // no runways defined
            return true;
        }
        for (GuiRunway rw : data.getRunways().values()) {
            if (activeLandingRunways != null && rw.isLandingActive() && activeLandingRunways.contains(rw.getCode())) {
                return true;
            }
            if (activeStartingRunways != null && rw.isStartingActive() && activeStartingRunways.contains(rw.getCode())) {
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
        if (pointDescr.matches("[A-Z0-9]*-RW[A-Z0-9]*")) {
            String airportCode = pointDescr.substring(0,pointDescr.indexOf("-"));
            if (!data.getAirportCode().equals(airportCode)) {
                throw new IllegalArgumentException("Wrong airport referenced in "+pointDescr+"!");
            }

            String runwayCode = pointDescr.substring(pointDescr.indexOf("-")+1+2); // +2 is for the prefix 'RW' in front of the number
            GuiRunway rw = data.getRunways().get(runwayCode);
            if (rw==null) {
                throw new IllegalArgumentException("Runway end for definition "+pointDescr+" not found!");
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
            float angle = Float.parseFloat(pointDescr.substring(pos + 2, pos2));
            String id = pointDescr.substring(pos2 + 1);
            if (data.getNavaidDB().getNavaid(id) == null) {
                throw new IllegalArgumentException("Navaid " + pointDescr + " not found!");
            }
            Point2D navaidPoint = data.getNavaidDB().getNavaid(id).getGeographicPosition();
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
     * This method can read the point coordinates ("lat,lon") directly as decimals as well as in degree, minute and seconds format.
     *
     * @param pointDescr
     * @return
     */
    private Point2D parsePoint(String pointDescr) {
        String lat = pointDescr.substring(0, pointDescr.indexOf(","));
        double latD = 0;
        if(lat.contains("°") && lat.contains("'") && lat.contains("''")) {
            // N47°54'26
            int sign = lat.substring(0,1).equalsIgnoreCase("N") ? 1 : -1;
            Double degrees = Double.parseDouble(lat.substring(1,lat.indexOf("°")).trim()) ;
            Double minutes = Double.parseDouble(lat.substring(lat.indexOf("°")+1,lat.indexOf("'")).trim()) ;
            Double seconds = Double.parseDouble(lat.substring(lat.indexOf("'")+1,lat.indexOf("''")).trim()) ;
            latD = (degrees + minutes / 60d + seconds / 3600d) * sign;

        } else if(lat.contains("°") && lat.contains("'")) {
            // N47°54.26
            int sign = lat.substring(0,1).equalsIgnoreCase("N") ? 1 : -1;
            Double degrees = Double.parseDouble(lat.substring(1,lat.indexOf("°")).trim()) ;
            Double minutes = Double.parseDouble(lat.substring(lat.indexOf("°")+1,lat.indexOf("'")).trim()) ;
            latD = (degrees + minutes / 60) * sign;

        } else {
            latD = Double.parseDouble(lat);
        }


        String lon = pointDescr.substring(pointDescr.indexOf(",") + 1);
        double lonD = 0;
        if(lon.contains("°") && lon.contains("'") && lon.contains("''")) {
            // E7°54'15
            int sign = lon.substring(0,1).equalsIgnoreCase("E") ? 1 : -1;
            Double degrees = Double.parseDouble(lon.substring(1,lon.indexOf("°")).trim()) ;
            Double minutes = Double.parseDouble(lon.substring(lon.indexOf("°")+1,lon.indexOf("'")).trim()) ;
            Double seconds = Double.parseDouble(lon.substring(lon.indexOf("'")+1,lon.indexOf("''")).trim()) ;

            lonD = (degrees + minutes / 60d + seconds / 3600d) * sign;

        } else if(lon.contains("°") && lon.contains("'")) {
            // E7°54.15
            int sign = lon.substring(0,1).equalsIgnoreCase("E") ? 1 : -1;
            Double degrees = Double.parseDouble(lon.substring(1,lon.indexOf("°")).trim()) ;
            Double minutes = Double.parseDouble(lon.substring(lon.indexOf("°")+1,lon.indexOf("'")).trim()) ;

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
}
