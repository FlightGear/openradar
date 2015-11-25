/**
 * Copyright (C) 2013,2015 Wolfram Wagner
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
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
/**
 * line
 * .begin "last"(previous end point), Navaid code or geo coordinates ("lat,lon")
 * .end (optional if angle and length is given) Navaid code or geo coordinates ("lat,lon")
 * .angle (displayed and used for calculation if .end has been omitted
 * .length (used for calculation if angle is given and .end has been omitted)
 * .beginOffset
 * .endOffset (if end is given)
 * .stroke (optional if differs from normal line) alternatives: "dashed","dots"
 * .lineWidth (optional)
 * .arrows (optional) "none","start","end","both"
 *
 *
 * @author Wolfram Wagner
 *
 */
public class StdRouteLine extends AStdRouteElement {

    private final Point2D geoStartPoint;
    private final Point2D geoEndPoint;
    private Double angle;
    private final Double length;
    private final Double startOffSet;
    private final Double endOffSet;
    private final String text;

    public StdRouteLine(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                        String start, String end, String angle, String length, String startOffset, String endOffset,
                        String arrows, String text,StdRouteAttributes attributes) {
        super(data, mapViewAdapter, route.getPoint(start,previous),arrows,attributes);

        this.geoStartPoint = route.getPoint(start,previous);
        if(end==null) {
            if(angle==null) {
                throw new IllegalArgumentException("Line: if end is omitted, ANGLE and length must be specified!");
            }
            if(length==null) {
                throw new IllegalArgumentException("Line: if end is omitted, angle and LENGTH must be specified!");
            }
            geoEndPoint = null;
        } else {
            geoEndPoint = route.getPoint(end, previous);
        }
        this.angle = angle !=null ? Double.parseDouble(angle)+magDeclination : null;
        this.length = length !=null ? Double.parseDouble(length) : null;
        this.startOffSet = startOffset !=null ? Double.parseDouble(startOffset) : 0 ;
        this.endOffSet = endOffset !=null ? Double.parseDouble(endOffset) : 0 ;
        this.text=text;
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D startPoint = getDisplayPoint(geoStartPoint);
        Point2D endPoint = null;
        if(geoEndPoint!=null) {
            endPoint = getDisplayPoint(geoEndPoint);
            angle = Converter2D.getDirection(startPoint, endPoint);
        } else {
            endPoint = Converter2D.getMapDisplayPoint(startPoint, angle, Converter2D.getFeetToDots(length*Units.NM/Units.FT, mapViewAdapter));
        }

        if(startOffSet!=null) {
            startPoint = Converter2D.getMapDisplayPoint(startPoint, angle, Converter2D.getFeetToDots(startOffSet*Units.NM/Units.FT, mapViewAdapter));
        }

        if(endOffSet!=null) {
            endPoint = Converter2D.getMapDisplayPoint(endPoint, angle-180, Converter2D.getFeetToDots(endOffSet*Units.NM/Units.FT, mapViewAdapter));
        }

        Path2D path = new Path2D.Double();
        if(text==null) {
            path.append(new Line2D.Double(startPoint, endPoint),false);
        } else {
            double length = startPoint.distance(endPoint);
            Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);

            double direction = Converter2D.getDirection(startPoint, endPoint);
            double dirTopLeftCorner = Converter2D.getDirection(new Point2D.Double(0, 0), new Point2D.Double(bounds.getWidth(), -1* bounds.getHeight()));
            double gap = 0;
            if(    direction >  360-dirTopLeftCorner
               || (direction < 180+dirTopLeftCorner && direction > 180-dirTopLeftCorner)
               ||  direction <     dirTopLeftCorner) {
               //  top // bottom
                gap = Math.abs(bounds.getHeight() / Math.cos(Math.toRadians(direction))) / 2 + 4 ;
            } else {
                // left right
                gap = Math.abs(bounds.getWidth() / Math.sin(Math.toRadians(direction))) / 2 + 4;
            }

            if(length*0.5 >  gap) {
                Point2D middlePoint1 = Converter2D.getMapDisplayPoint(startPoint, angle, length*0.5 - gap * Math.signum(length) );
                Point2D middlePoint2 = Converter2D.getMapDisplayPoint(startPoint, angle, length*0.5 + gap * Math.signum(length) );
                path.append(new Line2D.Double(startPoint, middlePoint1),false);
                path.append(new Line2D.Double(middlePoint2, endPoint),false);

               // System.out.println(String.format("%3.0f %4.1f %s",direction,gap,text));
                Point2D middlePoint = Converter2D.getMapDisplayPoint(startPoint, angle, length*0.5);
                g2d.drawString(text, (int)(middlePoint.getX()-bounds.getWidth()/2), (int)(middlePoint.getY()+bounds.getHeight()/2-2));

            } else {
                // skip text, paint line only
                path.append(new Line2D.Double(startPoint, endPoint),false);
            }
        }
        g2d.draw(path);


        if("both".equalsIgnoreCase(arrows) || "start".equalsIgnoreCase(arrows)) {
            double heading = angle!=null ? angle : Converter2D.getDirection(startPoint, endPoint);
            this.paintArrow(g2d, startPoint, heading, arrowSize, false); // start is not the tip
        }
        if("both".equalsIgnoreCase(arrows) || "end".equalsIgnoreCase(arrows)) {
            double heading = angle!=null ? angle : Converter2D.getDirection(startPoint,endPoint);
            this.paintArrow(g2d, endPoint, heading, arrowSize, true);
        }

        return path.getBounds2D();
    }

    @Override
    public Point2D getEndPoint() {
        if(geoEndPoint!=null) {
            return geoEndPoint;
        } else {
            return new IndirectPoint2D(mapViewerAdapter, geoStartPoint, angle, length);
        }
    }

    @Override
    public boolean contains(Point p) {
        return false;
    }
}
