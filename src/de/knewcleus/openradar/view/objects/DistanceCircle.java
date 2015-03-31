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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class DistanceCircle extends AViewObject {

    public enum Style { MINOR, PLAIN, IMPORTANT }
    private double radius;
    private AirportData data;
    
    private double x1=0;
    private double x2=0;
    private double y1=0;
    private double y2=0;
    private Point2D point;
    
    public DistanceCircle(AirportData data, Style style, double radius, int minScale, int maxScale) {
        super(Color.gray);
        this.data =data;
        this.radius = radius;
        setMinScalePath(minScale);
        setMaxScalePath(maxScale);
        
        this.font = new Font("Arial", Font.PLAIN, 9);
        
        switch(style) {
        case MINOR:
            color=Color.gray;
            this.stroke = new BasicStroke(0.3f);
            break;
        case PLAIN:
            color=Color.lightGray;
            this.stroke = new BasicStroke(0.4f);
            break;
        case IMPORTANT:
            color=Color.white;
            this.stroke = new BasicStroke(0.5f);
            break;
        }
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {
        
        double dotRadius = Converter2D.getFeetToDots(radius*Units.NM/Units.FT, mapViewAdapter);
        Point2D logPoint = mapViewAdapter.getProjection().toLogical(data.getAirportPosition());
        point = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint,null);

        x1 = Converter2D.getMapDisplayPoint(point, 270d, dotRadius).getX();
        y1 = Converter2D.getMapDisplayPoint(point, 0d, dotRadius).getY();
        x2 = Converter2D.getMapDisplayPoint(point, 90d, dotRadius).getX();
        y2 = Converter2D.getMapDisplayPoint(point, 180d, dotRadius).getY();

        path = new Path2D.Double();
        if(data.getRadarObjectFilterState("CIRCLES")) {
            path.append(new Ellipse2D.Double(x1, y1, x2-x1, y2-y1), false);
        }
    }
    @Override
    public void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        super.paint(g2d, mapViewAdapter);
        if(data.getRadarObjectFilterState("CIRCLES")) {
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
}
