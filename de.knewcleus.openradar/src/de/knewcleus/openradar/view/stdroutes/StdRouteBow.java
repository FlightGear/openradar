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

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class StdRouteBow extends AStdRouteElement {

    private final double radius;
    private final double startAngle;
    private final double extentAngle;
    private final String arrows;
    private Point2D endPoint=null;
    private final String text;

    public StdRouteBow(StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                             String center, String radius, String startAngle, String extentAngle,
                             String stroke, String lineWidth, String color, String arrows, String text) {

        super(mapViewAdapter, route.getPoint(center,previous),stroke,lineWidth,arrows,color);
        this.radius = Double.parseDouble(radius);
        this.startAngle = 90-Double.parseDouble(startAngle);
        this.extentAngle = -1 * Double.parseDouble(extentAngle);
        this.arrows = arrows;
        this.text = text;
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D center = getDisplayPoint(geoReferencePoint);
        double radiusDots = Converter2D.getFeetToDots(radius * Units.NM / Units.FT, mapViewAdapter);
        endPoint = mapViewAdapter.getProjection().toGeographical(mapViewAdapter.getDeviceToLogicalTransform().transform(Converter2D.getMapDisplayPoint(center, startAngle+extentAngle, radiusDots),null));

        if(color!=null) {
            g2d.setColor(color);
        }
        Path2D path = new Path2D.Double();
        if(text==null) {
            path.append(new Arc2D.Double(center.getX()-radiusDots, center.getY()-radiusDots,radiusDots*2,radiusDots*2,startAngle,extentAngle,Arc2D.OPEN), false);
        } else {
            g2d.setFont(g2d.getFont().deriveFont(10.0f));
            Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
            double direction = startAngle + extentAngle/2+90;
            double gapPix =  ( bounds.getHeight() + (bounds.getWidth()-bounds.getHeight()) * Math.cos(Math.toRadians(direction)) / 2 ) + 8;
            double gap = Math.toDegrees(Math.asin(gapPix/2/radiusDots));

            double extent = extentAngle/2 - gap * Math.signum(extentAngle);
            if( Math.abs(2 * gap) < Math.abs(extent) ) {
                path.append(new Arc2D.Double(center.getX()-radiusDots, center.getY()-radiusDots,radiusDots*2,radiusDots*2,startAngle,extent,Arc2D.OPEN), false);
                path.append(new Arc2D.Double(center.getX()-radiusDots, center.getY()-radiusDots,radiusDots*2,radiusDots*2,startAngle+extentAngle,-1*extent,Arc2D.OPEN), false);
                Point2D textPos = Converter2D.getMapDisplayPoint(center, 90 - (startAngle + extentAngle/2), radiusDots);
                g2d.drawString(text, (int)(textPos.getX()-bounds.getWidth()/2), (int)(textPos.getY()+bounds.getHeight()/2-2));
            } else {
                // skipt text, no space
                path.append(new Arc2D.Double(center.getX()-radiusDots, center.getY()-radiusDots,radiusDots*2,radiusDots*2,startAngle,extentAngle,Arc2D.OPEN), false);
            }
        }

        Stroke origStroke = g2d.getStroke();
        if(stroke!=null) {
            g2d.setStroke(stroke);
        }
        g2d.draw(path);

        if("both".equalsIgnoreCase(arrows) || "start".equalsIgnoreCase(arrows)) {
            double heading = 90 - startAngle + 90 * Math.signum(extentAngle);
            this.paintArrow(g2d, Converter2D.getMapDisplayPoint(center, 90-startAngle, Converter2D.getFeetToDots(radius*Units.NM/Units.FT,mapViewAdapter)), heading, arrowSize, false);
        }
        if("both".equalsIgnoreCase(arrows) || "end".equalsIgnoreCase(arrows)) {
            double heading = 90 - (startAngle + extentAngle) - 90 * Math.signum(extentAngle);
            this.paintArrow(g2d, Converter2D.getMapDisplayPoint(center, 90-startAngle-extentAngle, Converter2D.getFeetToDots(radius*Units.NM/Units.FT,mapViewAdapter)), heading, arrowSize, true);
        }

        g2d.setStroke(origStroke);

        return path.getBounds2D();
    }

    @Override
    public Point2D getEndPoint() {
        return endPoint;
    }
}