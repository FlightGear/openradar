/**
 * Copyright (C) 2016 Andreas Vogel
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
package de.knewcleus.openradar.view.stdroutes;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;


public class StdRouteWay  extends AStdRouteElement {

    private final Point2D geoStartPoint;
    private final Point2D geoEndPoint;
    private final double startOffset;
    private final double endOffset;
    private final String label;
    private final String height;
    private final String distance;
    private final String startHeading;
    private final String endHeading;

    public StdRouteWay(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                        String start, String end, String angle, String length, String startOffset, String endOffset,
                        String arrows, String label, String height, String distance, String heading, StdRouteAttributes attributes) {
        super(data, mapViewAdapter, route.getPoint(start,previous), arrows, attributes);

        this.geoStartPoint = route.getPoint(start,previous);
        if(end==null) {
            if(angle==null) {
                throw new IllegalArgumentException("Line: if end is omitted, ANGLE and length must be specified!");
            }
            if(length==null) {
                throw new IllegalArgumentException("Line: if end is omitted, angle and LENGTH must be specified!");
            }
            this.geoEndPoint = new IndirectPoint2D(mapViewerAdapter, this.geoStartPoint, Double.parseDouble(angle), Double.parseDouble(length));
        } else {
            this.geoEndPoint = route.getPoint(end, previous);
        }
        this.startOffset = startOffset !=null ? Double.parseDouble(startOffset) : 0;
        this.endOffset = endOffset !=null ? Double.parseDouble(endOffset) : 0;
        this.label = label;
        this.height = height;
        this.distance = distance;
        this.startHeading = (heading != null) ? heading + "�" : null;
        this.endHeading = (heading != null) ? String.format("%.0f�", (Double.parseDouble(heading) + 180) % 360) : null;
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        // start, end
        Point2D startPoint = getDisplayPoint(geoStartPoint);
        Point2D endPoint = getDisplayPoint(geoEndPoint);
        // angle
        double angle = StdRouteWay.getDirection(startPoint, endPoint);
        // offset
        if(this.startOffset!=0) {
            startPoint = Converter2D.getMapDisplayPoint(startPoint, angle, Converter2D.getFeetToDots(this.startOffset*Units.NM/Units.FT, mapViewAdapter));
        }
        if(this.endOffset!=0) {
            endPoint = Converter2D.getMapDisplayPoint(endPoint, angle-180, Converter2D.getFeetToDots(this.endOffset*Units.NM/Units.FT, mapViewAdapter));
        }
        // distance
        double distance = startPoint.distance(endPoint);
        if(distance>0) {
            // center
            Point2D centerPoint = Converter2D.midPoint(startPoint, endPoint);
            int c_x = (int)centerPoint.getX();
            int c_y = (int)centerPoint.getY();
            // Initialization
            int box_t = c_y;
            int box_b = c_y;
            int al = c_x;
            int ar = al;
            int border_x = 2;
            int border_y = 1;
            // init Transform
            AffineTransform oldTransform = g2d.getTransform();
            AffineTransform newTransform = new AffineTransform();
            // Font
            FontMetrics fm = g2d.getFontMetrics();
            // heading information
            int sh_w = this.startHeading != null ? fm.stringWidth(this.startHeading) : 0;
            int eh_w = this.endHeading   != null ? fm.stringWidth(this.endHeading) : 0;
            int sh_g = this.startHeading != null ? 2 * fm.getAscent() : 0;
            int eh_g = this.endHeading   != null ? 2 * fm.getAscent() : 0;
            int lh_w = 2 * fm.getAscent();
            int dh = (int) (distance / 2);
            int xl = c_x - dh;
            int xr = c_x + dh;
            int f = ((int)(((angle + 90) % 360) / 180));
            double headingRadians = Math.toRadians(f * 180);
            int sh_x = c_x - sh_w / 2;
            int sh_y = c_y + dh - lh_w - (sh_g - fm.getAscent()) / 2 - (f > 0 ? fm.getAscent() : sh_g / 4);
            int eh_x = c_x - eh_w / 2;
            int eh_y = c_y - dh + lh_w + (eh_g - fm.getAscent()) / 2 + (f == 0 ? fm.getAscent() : eh_g / 4);
            // draw label
            if((this.label != null) && (lh_w * 3 + sh_w + eh_w < distance)) {
                // label
                int label_dx = fm.stringWidth(this.label);
                int label_dy = (int)(fm.getAscent() * 4.0 / 5.0 + 0.5); // approximation to get rid of the space above letters
                int arrows_dx = (int)(label_dy + 1) / 2;
                int label_x = c_x - label_dx / 2;
                int label_y = c_y + arrows_dx;
                // surrounding box
                int box_l = label_x - border_x;
                int box_r = label_x + label_dx + border_x;
                box_b = label_y + border_y;
                box_t = label_y - label_dy - border_y;
                // arrows
                int arrow_left = ("both".equalsIgnoreCase(arrows) || "start".equalsIgnoreCase(arrows)) ? arrows_dx : 0;
                int arrow_right = ("both".equalsIgnoreCase(arrows) || "end".equalsIgnoreCase(arrows)) ? arrows_dx : 0;
                al = box_l - arrow_left;
                ar = box_r + arrow_right;

                if (ar-al < distance) {
                    // draw frame with arrows(s)
                    Path2D path = new Path2D.Double();
                    path.append(new Line2D.Double(box_l, box_b, box_r, box_b),false);
                    path.append(new Line2D.Double(box_r, box_b, ar, c_y),false);
                    path.append(new Line2D.Double(ar, c_y, box_r, box_t),false);
                    path.append(new Line2D.Double(box_r, box_t, box_l, box_t),false);
                    path.append(new Line2D.Double(box_l, box_t, al, c_y),false);
                    path.append(new Line2D.Double(al, c_y, box_l, box_b),false);
                    Stroke stroke_backup = g2d.getStroke();
                    g2d.setStroke(new BasicStroke (1));
                    newTransform.setToRotation(Math.toRadians(angle - 90), c_x, c_y);
                    g2d.transform(newTransform);
                    g2d.draw(path);
                    g2d.setStroke(stroke_backup);

                    // draw label
                    if (angle > 180) {
                        newTransform.setToRotation(Math.toRadians(180), c_x, c_y);
                        g2d.transform(newTransform);
                    }
                    g2d.drawString(this.label, label_x, label_y);
                } else {
                    // not enough distance for label
                    box_t = c_y;
                    box_b = c_y;
                    al = c_x;
                    ar = al;
                }
            }
            // draw simple arrows
            if(al == ar) {
                if("both".equalsIgnoreCase(arrows) || "start".equalsIgnoreCase(arrows)) {
                    StdRouteWay.paintSmallArrow(g2d, startPoint, angle, arrowSize, true);
                }
                if("both".equalsIgnoreCase(arrows) || "end".equalsIgnoreCase(arrows)) {
                    StdRouteWay.paintSmallArrow(g2d, endPoint, angle, arrowSize, false);
                }
            }

            Path2D path = new Path2D.Double();
            // draw route line(s)
            if ((sh_w + lh_w < al - xl) && (sh_w > 0)) {
                path.append(new Line2D.Double(xl, c_y, xl + lh_w, c_y),false);
                xl += sh_w + lh_w;
                // draw heading label
                g2d.setTransform(oldTransform);
                g2d.rotate(Math.toRadians(angle), c_x, c_y);
                g2d.rotate(headingRadians, c_x, sh_y);
                g2d.drawString(this.startHeading, sh_x, sh_y);

            }
            if ((eh_w + lh_w < xr - ar) && (eh_w > 0)) {
                path.append(new Line2D.Double(xr - lh_w, c_y, xr, c_y),false);
                xr -= eh_w + lh_w;
                // draw heading label
                g2d.setTransform(oldTransform);
                g2d.rotate(Math.toRadians(angle), c_x, c_y);
                g2d.rotate(headingRadians, c_x, eh_y);
                g2d.drawString(this.endHeading, eh_x, eh_y);
            }
            path.append(new Line2D.Double(xl, c_y, al, c_y),false);
            path.append(new Line2D.Double(ar, c_y, xr, c_y),false);
            newTransform.setToRotation(Math.toRadians(angle - 90), c_x, c_y);
            g2d.setTransform(newTransform);
            g2d.draw(path);
            if (angle > 180) {
                newTransform.setToRotation(Math.toRadians(180), c_x, c_y);
                g2d.transform(newTransform);
            }
            // draw height
            if((this.height!=null) && (fm.stringWidth(this.height) < distance)) {
                g2d.drawString(this.height, (int) (c_x - fm.stringWidth(this.height) / 2), box_t - 2 * border_y);
            }
            // draw distance
            if((this.distance!=null) && (fm.stringWidth(this.distance) < distance)) {
                g2d.drawString(this.distance, (int) (c_x - fm.stringWidth(this.distance) / 2), box_b + border_y + fm.getAscent());
            }
            g2d.setTransform(oldTransform);
        }
        Rectangle2D bounds = new Rectangle2D.Double(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY());
        return bounds.getBounds2D();
    }

    @Override
    public Point2D getEndPoint() {
        return geoEndPoint;
    }

    @Override
    public boolean contains(Point p) {
        return false;
    }

    protected static void paintSmallArrow(Graphics2D g2d, Point2D tipPoint, double heading, int size, boolean start) {

        if(start) {
            heading = heading+180;
            //tipPoint = Converter2D.getMapDisplayPoint(tipPoint, heading, size);
        }

        Point2D point1 = Converter2D.getMapDisplayPoint(tipPoint, heading-180+20, size);
        Point2D point2 = Converter2D.getMapDisplayPoint(tipPoint, heading-180-20, size);

        Path2D path = new Path2D.Double();
        path.append(new Line2D.Double(tipPoint, point1),false);
        path.append(new Line2D.Double(point1, point2),true);
        path.closePath();

        g2d.fill(path);

    }

    protected static double getDirection (Point2D point1, Point2D point2) {
        double dx = point2.getX()-point1.getX();
        double dy = -1*(point2.getY()-point1.getY());
        return getDirection(dx, dy);
    }
    protected static double getDirection (double dx, double dy) {

        double distance = Math.sqrt(dx*dx + dy*dy);
        double angle = 0;
        if(distance!=0) {
            if(dx>=0 && dy>=0) angle = Math.asin(dx/distance)/2d/Math.PI*360d;
            if(dx>=0 && dy<=0) angle = 180-Math.asin(dx/distance)/2d/Math.PI*360d;
            if(dx<=0 && dy<=0) angle = 180+-1*Math.asin(dx/distance)/2d/Math.PI*360d;
            if(dx<=0 && dy>=0) angle = 360+Math.asin(dx/distance)/2d/Math.PI*360d;
        }
        return angle % 360;
    }
}
