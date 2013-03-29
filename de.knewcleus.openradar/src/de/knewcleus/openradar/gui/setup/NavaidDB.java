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
package de.knewcleus.openradar.gui.setup;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.fgfs.navdata.impl.Intersection;
import de.knewcleus.fgfs.navdata.impl.NDB;
import de.knewcleus.fgfs.navdata.impl.VOR;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.stdroutes.StdRoute;
/**
 * This class is holds references to all navaids in range and the standard routes. It is used to know if a navaid
 * is part of a route, if it should be highlighted and in which color.
 *
 * @author Wolfram Wagner
 *
 */
public class NavaidDB {
    private volatile IIntersection selectedNavaid = null;
    private final Map<String, List<IIntersection>> navaidMap = new TreeMap<String, List<IIntersection>>();
    private List<StdRoute> stdRoutes = new ArrayList<StdRoute>();

    private final static Logger log = Logger.getLogger(NavaidDB.class.toString());

    public synchronized void registerNavaid(IIntersection navPoint) {
        List<IIntersection> list = navaidMap.get(navPoint.getIdentification());
        if(list==null) {
            list = new ArrayList<IIntersection>();
            navaidMap.put(navPoint.getIdentification(), list);
        }
        list.add(navPoint);
    }

    /**
     * Navaids can share names (for instance MTR near EDDF) So it is possible to add (FIX), (NDB) or (VOR) in front to specify the type of the navaid...
     *
     * @param id
     * @return
     */
    public synchronized IIntersection getNavaid(String id) {
        String type = null;
        if(id.contains("(") && id.contains("(")) {
            try {
                type = id.substring(id.indexOf("(")+1, id.indexOf(")")).trim();
                id = id.substring(id.indexOf(")")+1).trim();
            } catch(Exception e) {
                log.severe("Skipping navpoint highlighting! Cannot parse: "+id);
            }
        }
        List<IIntersection> list = navaidMap.get(id);
        if(list!=null && !list.isEmpty()) {
            if(list.size()==1) {
                return list.get(0);
            } else {
                if(type==null) {
                    log.severe("Navpoint highlighting: ID "+id+" exists multiple times. Taking first! Try to add (FIX),(NDB) or (VOR) in front of it!");
                    return list.get(0);
                } else {
                    for(IIntersection navPoint : list) {
                        if(type.equals("ADD") && navPoint instanceof AdditionalFix) {
                            return navPoint;
                        }
                        if(type.equals("FIX") && navPoint instanceof Intersection || navPoint instanceof AdditionalFix) {
                            return navPoint;
                        }
                        if(type.equals("NDB") && navPoint instanceof NDB) {
                            return navPoint;
                        }
                        if(type.equals("VOR") && navPoint instanceof VOR) {
                            return navPoint;
                        }
                        if(type.equals("APT") && navPoint instanceof Aerodrome) {
                            return navPoint;
                        }
                    }
                    log.warning("Navpoint highlighting: ID "+id+" of type "+type+" not found!");
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    // highlighting
    public synchronized IIntersection highlight(String id) {
        IIntersection navPoint = getNavaid(id);
        if(navPoint!=null) {
            navPoint.setHighlighted(true);
        }
        return navPoint;
    }

    public synchronized void resetHighlighting() {
        for(List<IIntersection> navPointList : navaidMap.values()) {
            for(IIntersection navPoint : navPointList) {
                navPoint.setHighlighted(false);
            }
        }
    }


    // navaid selection

    public synchronized boolean isNavaidSelected(IIntersection navPoint) {
        return selectedNavaid!=null && selectedNavaid==navPoint;
    }

    public synchronized List<StdRoute> getStdRoutes() {
        return stdRoutes;
    }


    public synchronized void setStdRoutes(List<StdRoute> stdRoutes) {
        this.stdRoutes = stdRoutes;
    }

    public synchronized void selectNavaid(IIntersection navPoint) {
        selectedNavaid=navPoint;
    }

    public synchronized boolean isPartOfRoute(AirportData data, IIntersection navPoint) {
//        IIntersection navPoint = getNavaid(id);
        if(navPoint==null) return false;
        for(StdRoute route : stdRoutes) {
            if(route.isVisible(data)) {
                if(route.containsNavaid(navPoint)) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized Color getNavaidHighlightColor(AirportData data,  IIntersection navPoint) {
        if(navPoint==null) return null;

        if(isNavaidSelected(navPoint)) {
            return Palette.NAVAID_HIGHLIGHT;
        }

        if(navPoint.isHighlighted()) {
            return Palette.NAVAID_HIGHLIGHT;
        }


        for(StdRoute route : stdRoutes) {
            if(route.isVisible(data)) {
                if(route.containsNavaid(navPoint)) {
                    if(route.getActiveLandingRunways()!=null || route.getActiveStartingRunways()!=null) {
                        return route.getNavaidColor();
                    } else {
                        return Palette.NAVAID_HIGHLIGHT;
                    }
                }
            }
        }
        return null;
    }

    public boolean hasRoutes() {
        return !stdRoutes.isEmpty();
    }

    public void addPoint(String code, String point) {
        registerNavaid(new AdditionalFix(code, point));
    }


}