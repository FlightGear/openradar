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
package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

import de.knewcleus.fgfs.navdata.impl.MarkerBeacon;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.gui.setup.RunwayData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class IlsMarkerBeacon extends AViewObject {

    private MarkerBeacon mb;
    private final AirportData data;

    public IlsMarkerBeacon(MarkerBeacon mb, AirportData data) {
        super(Palette.GLIDESLOPE);
        this.stroke = new BasicStroke(0.3f);
        this.mb=mb;
        this.data = data;
        switch(mb.getType()) {
        case Inner:
            color = Color.white;
            break;
        case Middle:
            color = Color.orange;
            break;
        case Outer:
            color = Palette.GLIDESLOPE;
            break;
        }

    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewerAdapter) {
        RunwayData rwd = mb.getRunwayEnd()==null && mb.getAirportID().equals(data.getAirportCode()) ? data.getRunwayData(mb.getRunwayID()):null;
        boolean enabled = rwd==null || (rwd!=null && rwd.isLandingEnabled()); 
        
        if (!enabled || mb.getRunwayEnd()==null ||  !mb.getRunwayEnd().isLandingActive() || mb.getRunwayEnd().getGlideslope()==null){
            path=null;
            return;
        }
        
        float reverseHeading = mb.getRunwayEnd().getTrueHeading()+180;
        
        
        Point2D startPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading-90, 250d / mapViewerAdapter.getLogicalScale());
        Point2D endPoint = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading+90, 250d / mapViewerAdapter.getLogicalScale());

        Point2D controlPoint1 = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading, 70d / mapViewerAdapter.getLogicalScale());
        Point2D controlPoint2 = Converter2D.getMapDisplayPoint(newDisplayPosition, reverseHeading+180, 70d / mapViewerAdapter.getLogicalScale());
        
        path = new Path2D.Double();
        path.append(new QuadCurve2D.Double(startPoint.getX(),startPoint.getY(),
                                           controlPoint1.getX(),controlPoint1.getY(),
                                           endPoint.getX(),endPoint.getY()),false);
        path.append(new QuadCurve2D.Double(startPoint.getX(),startPoint.getY(),
                controlPoint2.getX(),controlPoint2.getY(),
                endPoint.getX(),endPoint.getY()),false);
    }
}
