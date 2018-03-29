/**
 * Copyright (C) 2018 Wolfram Wagner 
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

import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class RadarRangeCircle extends AViewObject {

    private double radius;
    private AirportData data;
    
    private double x1=0;
    private double x2=0;
    private double y1=0;
    private double y2=0;
    private Point2D point;
    
    public RadarRangeCircle(AirportData data) {
        super(Palette.GRAY);
        this.data =data;
        setMinScalePath(0);
        setMaxScalePath(Integer.MAX_VALUE);
        
        this.font = new Font("Arial", Font.PLAIN, 9);
        
        color=Palette.RADAR_RANGE_CIRCLE;
        this.stroke = new BasicStroke(0.3f);
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        radius = data.getFlightStripRadarRange();
        double dotRadius = Converter2D.getFeetToDots(radius*Units.NM/Units.FT, mapViewAdapter);
        Point2D logPoint = mapViewAdapter.getProjection().toLogical(data.getAirportPosition());
        point = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint,null);

        x1 = Converter2D.getMapDisplayPoint(point, 270d, dotRadius).getX();
        y1 = Converter2D.getMapDisplayPoint(point, 0d, dotRadius).getY();
        x2 = Converter2D.getMapDisplayPoint(point, 90d, dotRadius).getX();
        y2 = Converter2D.getMapDisplayPoint(point, 180d, dotRadius).getY();

        path = new Path2D.Double();
        path.append(new Ellipse2D.Double(x1, y1, x2-x1, y2-y1), false);
    }
    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
    	if(radius!=data.getFlightStripRadarRange()) {
    		constructPath(mapViewAdapter.getLogicalOrigin(), mapViewAdapter.getLogicalOrigin(), mapViewAdapter);
    	}
     	super.paint(g2d, mapViewAdapter);
        
        g2d.setColor(color);
        double currentScale = mapViewAdapter.getLogicalScale();
        if (minScalePath < currentScale && maxScalePath > currentScale) {
            if (font != null)
                g2d.setFont(font);
            
            String sRadius = String.format("%1.0f", radius);
            g2d.drawString(sRadius, (float) point.getX(), (float) y1+10);
            g2d.drawString(sRadius, (float) point.getX(), (float) y2-4);
            g2d.drawString(sRadius, (float) x1+4, (float) point.getY());
            g2d.drawString(sRadius, (float) x2- (radius>99?20:14), (float) point.getY());
        }
    }
}
