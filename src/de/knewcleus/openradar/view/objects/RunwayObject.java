/**
 * Copyright (C) 2012,2013,2015 Wolfram Wagner
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
package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.Graphics2D;
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
        super(Color.lightGray);
        this.data = data;
        this.rwy = rwy;

        setMinScalePath(0);
        setMaxScalePath(10);
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mva) {

        activeRw=false;
        GuiRunway grw = data.getRunways().get(rwy.getDesignation());
        color = Palette.RUNWAY;
        if(grw!=null) {
            boolean landingActive = grw.isLandingActive();
            boolean startingActive = grw.isStartingActive();

            if(landingActive || startingActive) {
                color = new Color(60,60,80);
                activeRw=true;
            }
        }
        setMaxScalePath(500);
        double runwayLength = rwy.getLength()/Units.FT;
        double runwayWidth = rwy.getWidth()/Units.FT; 
        float runwayHeading = rwy.getTrueHeading();
        double runwayHeadingOrtho = runwayHeading+90;
        this.fillPath=true;
        
        path = new Path2D.Double();
        Point2D refPoint = Converter2D.getMapDisplayPoint(rwy.getGeographicCenter(), mva);
        
        Point2D startPoint= Converter2D.getMapDisplayPoint(refPoint, runwayHeadingOrtho, Converter2D.getFeetToDots(runwayWidth/2,mva));
        startPoint = Converter2D.getMapDisplayPoint(startPoint, runwayHeading-180, Converter2D.getFeetToDots(runwayLength/2,mva));
        
        Point2D endPoint = Converter2D.getMapDisplayPoint(startPoint, runwayHeading,  Converter2D.getFeetToDots(runwayLength,mva));
        path.append(new Line2D.Double(startPoint, endPoint), true);
        
        startPoint = endPoint;
        endPoint = Converter2D.getMapDisplayPoint(startPoint, runwayHeadingOrtho+180,  Converter2D.getFeetToDots(runwayWidth,mva));
        path.append(new Line2D.Double(startPoint, endPoint), true);
        
        startPoint = endPoint;
        endPoint = Converter2D.getMapDisplayPoint(startPoint, runwayHeading+180,  Converter2D.getFeetToDots(runwayLength,mva));
        path.append(new Line2D.Double(startPoint, endPoint), true);
        
        startPoint = endPoint;
        endPoint = Converter2D.getMapDisplayPoint(startPoint, runwayHeadingOrtho,  Converter2D.getFeetToDots(runwayWidth, mva));
        path.append(new Line2D.Double(startPoint, endPoint), true);
        
        path.closePath();
    }
    public synchronized boolean isActiveRw() {
        return activeRw;
    }

    @Override
        public synchronized void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
            // TODO Auto-generated method stub
            super.paint(g2d, mapViewAdapter);
        }
    
}
