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
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.gui.setup.AirportData;
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
public class StdRouteText extends AStdRouteElement {

    private Double angle;
    private final String text;
    private final boolean clickable;
    private volatile Rectangle2D bounds;
    private volatile Rectangle2D extBounds;

    public StdRouteText(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                        String position, String angle, String alignHeading, boolean clickable, String text, StdRouteAttributes attributes) {
        super(data, mapViewAdapter, route.getPoint(position,previous),null,attributes);

        if(alignHeading!=null) {
            this.angle = adaptAngle(Double.parseDouble(alignHeading)+magDeclination);
        } else {
            this.angle = angle !=null ? Double.parseDouble(angle)+magDeclination : 0;
        }
        this.clickable=clickable;
        this.text=text;
    }

    public static Double adaptAngle(double angle) {
        angle = Line.normalizeLineAngle180(angle-90);
        while(angle>90) {
            angle = angle - 180;
        }
        while(angle<-90) {
            angle = angle + 180;
        }
        return angle;
    }

    @Override
    public synchronized Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D displayPoint = getDisplayPoint(geoReferencePoint);
        AffineTransform oldTransform = g2d.getTransform();
        AffineTransform newTransform = new AffineTransform();
        newTransform.setToRotation(Math.toRadians(angle), displayPoint.getX(), displayPoint.getY());
        g2d.transform(newTransform);

        bounds = g2d.getFontMetrics().getStringBounds(text, g2d);
        g2d.drawString(text, (int)(displayPoint.getX()-bounds.getWidth()/2), (int)(displayPoint.getY()-2));

        g2d.setTransform(oldTransform);

        bounds.setRect(displayPoint.getX(), displayPoint.getY(),bounds.getWidth(),bounds.getHeight());
        extBounds = new Rectangle2D.Double(displayPoint.getX()-bounds.getWidth(),displayPoint.getY()-bounds.getHeight(),2*bounds.getWidth(),2*bounds.getHeight());
        
        return bounds;
    }

    @Override
    public Point2D getEndPoint() {
        return geoReferencePoint;
    }

    @Override
    public synchronized boolean contains(Point e) {
        // ! route.isVisible(master) || 
        if(!clickable || extBounds==null) return false;
        return extBounds.contains(e);
    }
}
