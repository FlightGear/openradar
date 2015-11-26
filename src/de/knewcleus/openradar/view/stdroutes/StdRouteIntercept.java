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
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.apache.log4j.Logger;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;
 /*
 * @author Wolfram Wagner
 *
 */
public class StdRouteIntercept extends AStdRouteElement {

    private final Point2D geoStartPoint;
    private final Point2D geoStartBowPoint;
    private final Double startHeading;
    private final Point2D geoEndPoint;
    private final Double radius;
    private final Double radial;
    private final Double endHeading;
    private final Double startOffSet;
    private final Double endOffSet;
    private final String text;
    private final String direction;

    private final Double turnAngle;

    public StdRouteIntercept(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                        String start, String startOffset, String startHeading, String startBow, String radius, String speed, String end, String radial, String endHeading, String direction, String endOffset,
                        String arrows, String text,StdRouteAttributes attributes) {
        super(data, mapViewAdapter, route.getPoint((start != null ? start : startBow),previous),arrows, attributes);

        this.geoStartPoint = start!=null ? route.getPoint(start,previous) : null;
        this.geoStartBowPoint = startBow!=null ? route.getPoint(startBow,previous) : null;
        if(geoStartPoint!=null && geoStartBowPoint!=null) {
            Logger.getLogger(this.getClass()).trace("start and startBow is given, do not check if first line is a tangent to the bow");
        }
        this.startOffSet = startOffset !=null ? Double.parseDouble(startOffset) : 0 ;
        this.startHeading = Line.normalizeLineAngle180(90-(Double.parseDouble(startHeading)+magDeclination)); // magnetic to screen angles
        if(speed==null) {
            this.radius = radius !=null ? Double.parseDouble(radius) : 1.16d ;
        } else {
            if(radius!=null) {
                Logger.getLogger(this.getClass()).info("radius and speed is given to define intercept turn rate. Ignoring radius...");
            }
            double dSpeed = Double.parseDouble(speed) / 60; // miles per minute
            double circumference = dSpeed * 2;
            this.radius = circumference / 2 / Math.PI;
        }
        if(geoStartPoint!=null && geoStartBowPoint==null && radius==null) {
            Logger.getLogger(this.getClass()).trace("start given but no radius nor startBow, assuming radius of 1.16 NM");
        }
        this.geoEndPoint = route.getPoint(end,previous);
        if(endHeading!=null) {
            this.endHeading = Line.normalizeLineAngle180(90-(Double.parseDouble(endHeading)+magDeclination)); // magnetic to screen angles
            this.radial = Line.normalizeLineAngle180(this.endHeading+180);
        } else {
            this.radial = Line.normalizeLineAngle180(90-(Double.parseDouble(radial)+magDeclination)); // magnetic to screen angles
            this.endHeading = Line.normalizeLineAngle180(this.radial+180);
        }
        this.direction = direction!=null && !direction.equalsIgnoreCase("auto") ? direction : determineDirection();
        this.endOffSet = endOffset !=null ? Double.parseDouble(endOffset) : 0 ;
        this.text=text;
        turnAngle = this.radial-this.startHeading;
    }

    private String determineDirection() {
        return turnAngle > 0 ? "left" : "right";
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D firstLineStartPoint = geoStartPoint!=null ? getDisplayPoint(geoStartPoint) : null;
        if(startOffSet!=null && firstLineStartPoint!=null) {
            double length = Converter2D.getFeetToDots(startOffSet*Units.NM/Units.FT, mapViewAdapter);
            firstLineStartPoint = Converter2D.getMapDisplayPoint(firstLineStartPoint, 90-startHeading, length);
        }
        Point2D bowStartPoint = geoStartBowPoint!=null ? getDisplayPoint(geoStartBowPoint) : null;
        Point2D secondLineEndPoint = getDisplayPoint(geoEndPoint);

        if(endOffSet!=null) {
            double length = Converter2D.getFeetToDots(endOffSet*Units.NM/Units.FT, mapViewAdapter);
            secondLineEndPoint = Converter2D.getMapDisplayPoint(secondLineEndPoint, 90 - radial, length);
        }
        // construction

        Point2D bowCenter = null;

        if(firstLineStartPoint!=null && bowStartPoint==null) {
            // line to start of bow
            Double radiusDots = Converter2D.getFeetToDots(radius*Units.NM/Units.FT, mapViewAdapter);
            // find the center of the circle
            Line headingLine = new Line(firstLineStartPoint, startHeading);
            Line radialLine = new Line(secondLineEndPoint, radial);
            // construct the parallel lines at the inside and their crossing point => center of bow
            boolean right = direction.equalsIgnoreCase("right");
            Line headingParallel = headingLine.getParallelLine(right?-90:90,radiusDots);
            Line radialParallel = radialLine.getParallelLine(right?90:-90,radiusDots); // radial is pointing backward


            bowCenter = headingParallel.getIntersectionWith(radialParallel);
            if(bowCenter ==null) {
                // parallel heading and radial...
                Line radialNormal = new Line(secondLineEndPoint, radial + 90 );
                Point2D endProjection = headingLine.getIntersectionWith(radialNormal);

                if(headingLine.getM()!=null) {
                    // not vertical
                    if(startHeading>90 || startHeading <-90) {
                        // take the point with lower x and middle between lines
                        if(firstLineStartPoint.getX() < endProjection.getX()) {
                            bowStartPoint = firstLineStartPoint;
                        } else {
                            bowStartPoint = endProjection;
                        }
                    } else {
                        // take the point with greater x and middle between lines
                        if(firstLineStartPoint.getX() > endProjection.getX()) {
                            bowStartPoint = firstLineStartPoint;
                        } else {
                            bowStartPoint = endProjection;
                        }
                    }
                } else {
                    // vertical line
                    if(startHeading<0) {
                        // take the point with lower y and middle between lines
                        if(firstLineStartPoint.getY() > endProjection.getY()) {
                            bowStartPoint = firstLineStartPoint;
                        } else {
                            bowStartPoint = endProjection;
                        }
                    } else {
                        // take the point with greater y and middle between lines
                        if(firstLineStartPoint.getY() < endProjection.getY()) {
                            bowStartPoint = firstLineStartPoint;
                        } else {
                            bowStartPoint = endProjection;
                        }
                    }
                }
            } else {
               // normal case: heading and radial are not parallel

                // find the startPoint of the bow
                Line headingNormal = headingLine.getNormal(bowCenter);
                bowStartPoint = headingLine.getIntersectionWith(headingNormal);
            }

        }

        // 1: Find center of circle and its radius

        Line headingLine = new Line(bowStartPoint, startHeading);

        /* There are two mid-angle lines between heading and radial, and it is important
         * to take the right one. In out implementation the choosen line depends on the
         * direction of the radial line and on the turn */

        Line radialLine = new Line(secondLineEndPoint, radial);

        // line intersection exists
        Line middleLine = headingLine.getMidAngleLine(radialLine);

        // construct the normals to the line and their intersection with the middle line
        Line headingNormal = headingLine.getNormal(bowStartPoint);

        bowCenter = headingNormal.getIntersectionWith(middleLine);
        if(bowCenter==null) {
            // heading normal and middle line are parallel
            Line l = new Line(bowStartPoint, secondLineEndPoint);
            Double x =( secondLineEndPoint.getX()-bowStartPoint.getX())/2;
            Double y= l.getF(x);
            bowCenter = new Point2D.Double(x, y);
        }
        Double radiusDots = headingNormal.getDistance(bowStartPoint, bowCenter);

        // 2: Calculate the start and end angle of the bow
        Double startAngle = Converter2D.getDirection(bowCenter, bowStartPoint);

        Line radialNormal = new Line(bowCenter, radial + 90 );
        Point2D bowEndPoint = radialNormal.getIntersectionWith(radialLine);
        Double endAngle = Converter2D.getDirection(bowCenter, bowEndPoint);

        if(direction.equalsIgnoreCase("right")) {
            if(endAngle<startAngle) {
                endAngle=endAngle+360;
            }
        } else {
            if(endAngle>startAngle) {
                endAngle=endAngle-360;
            }
        }
        Double extentAngle = endAngle - startAngle;

        // convert the angles to screen angles
        startAngle = 90 - startAngle;
        extentAngle = -1 * extentAngle;



        // we have all we need: the bow center, its radius, the begin and end angles of the bow,
        // and the point at the end of the bow, the start point of the line to the radial

        Path2D path = new Path2D.Double();

//path.append(new Line2D.Double(bowStartPoint, bowCenter),false);
//path.append(new Line2D.Double(endPoint, radialNormal.getPoint()),false);


        // the line to start
        if (firstLineStartPoint!=null) {
            path.append(new Line2D.Double(firstLineStartPoint, bowStartPoint),false);
        }

        // the circle bow
        path.append(new Arc2D.Double(bowCenter.getX()-radiusDots, bowCenter.getY()-radiusDots,radiusDots*2,radiusDots*2,startAngle,extentAngle,Arc2D.OPEN), false);

        double length = bowEndPoint.distance(secondLineEndPoint);

        // the end line
        if(text==null) {
            path.append(new Line2D.Double(bowEndPoint, secondLineEndPoint),false);
        } else {
            Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(text, g2d);

            double direction = Converter2D.getDirection(bowEndPoint, secondLineEndPoint);
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
                double lineDir = 90-radial + 180 ;

                Point2D middlePoint1 = Converter2D.getMapDisplayPoint(bowEndPoint, lineDir, length*0.5 - gap * Math.signum(length) );
                Point2D middlePoint2 = Converter2D.getMapDisplayPoint(bowEndPoint, lineDir, length*0.5 + gap * Math.signum(length) );
                path.append(new Line2D.Double(bowEndPoint, middlePoint1),false);
                path.append(new Line2D.Double(middlePoint2, secondLineEndPoint),false);

               // System.out.println(String.format("%3.0f %4.1f %s",direction,gap,text));
                Point2D middlePoint = Converter2D.getMapDisplayPoint(bowEndPoint, lineDir, length*0.5);
                g2d.drawString(text, (int)(middlePoint.getX()-bounds.getWidth()/2), (int)(middlePoint.getY()+bounds.getHeight()/2-2));

            } else {
                // skip text, paint line only
                path.append(new Line2D.Double(bowEndPoint, secondLineEndPoint),false);
            }
        }
        g2d.draw(path);

        if("both".equalsIgnoreCase(arrows) || "start".equalsIgnoreCase(arrows)) {
            if (firstLineStartPoint!=null) {
                this.paintArrow(g2d, firstLineStartPoint, 90-startHeading, arrowSize, false);
            } else if (bowStartPoint!=null) {
                this.paintArrow(g2d, bowStartPoint, 90-startHeading, arrowSize, false);
            }
        }
        if("both".equalsIgnoreCase(arrows) || "end".equalsIgnoreCase(arrows)) {
            this.paintArrow(g2d, secondLineEndPoint,  90-radial+180, arrowSize, true);
        }
        
        return path.getBounds2D();
    }

    @Override
    public Point2D getEndPoint() {
        return geoEndPoint;
    }

    @Override
    public boolean contains(Point p) {
        return false;
    }
}
