/**
 * Copyright (C) 2015 Wolfram Wagner
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
package de.knewcleus.openradar.view.objects;

import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.xplane.Runway;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.status.runways.GuiRunway;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RunwayObject extends AViewObject {

    private final AirportData data;
    private final Runway rwy;
    private boolean activeRw = false;

    public RunwayObject(AirportData data, Runway rwy) {
        super(Palette.RUNWAY);
        this.data = data;
        this.rwy = rwy;

        setMinScalePath(0);
        setMaxScalePath(10);
    }

    @Override
    public synchronized void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mva) {

        setMaxScalePath(500);

        double runwayLength = rwy.getLength()/Units.FT;
        double runwayWidth = rwy.getWidth()/Units.FT;
        float runwayHeading = rwy.getTrueHeading();
        double runwayHeadingOrtho = runwayHeading+90;

        Point2D refPoint = Converter2D.getMapDisplayPoint(rwy.getGeographicCenter(),mva);

        // logP1 requires two calculations, to get to one corner
        Point2D devP1 = Converter2D.getMapDisplayPoint(refPoint, runwayHeadingOrtho, Converter2D.getFeetToDots(runwayWidth/2,mva));
        devP1 = Converter2D.getMapDisplayPoint(devP1, runwayHeading+180,  Converter2D.getFeetToDots(runwayLength/2,mva));
        Point2D devP2 = Converter2D.getMapDisplayPoint(devP1, runwayHeading,  Converter2D.getFeetToDots(runwayLength,mva));
        Point2D devP3 = Converter2D.getMapDisplayPoint(devP2, runwayHeadingOrtho+180,  Converter2D.getFeetToDots(runwayWidth,mva));
        Point2D devP4 = Converter2D.getMapDisplayPoint(devP3, runwayHeading+180,  Converter2D.getFeetToDots(runwayLength,mva));
        
        
        this.fillPath=true;

        path = new Path2D.Double();
        path.append(new Line2D.Double(devP1, devP2), true);
        path.append(new Line2D.Double(devP2, devP3), true);
        path.append(new Line2D.Double(devP3, devP4), true);
        path.append(new Line2D.Double(devP4, devP1), true);
        path.closePath();
    }

    public synchronized boolean isActiveRw() {
        color = Palette.RUNWAY;
    	activeRw=false;
    	if(rwy.getAirportID().equals(data.getAirportCode())) {
	        if(isActive(rwy.getEndA().getRunwayID()) || isActive(rwy.getEndB().getRunwayID())) {
                color = Palette.RUNWAY_ACTIVE;
                activeRw=true;
            }
// System.out.println(rwy.getAirportID()+" "+rwy.getDesignation()+" "+activeRw);
    	}
        return activeRw;
    }

    private boolean isActive(String runwayCode) {
	    GuiRunway grw = data.getRunways().get(runwayCode);
	    if(grw!=null) {
	        boolean landingActive = grw.isLandingActive();
	        boolean startingActive = grw.isStartingActive();
	
	        if(landingActive || startingActive) {
	        	return true;
	        }
	    }
	    return false;
    }
}
