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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
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
public class StdRouteMinAltitude extends AStdRouteElement {

    private final String major;
    private final String minor;

    public StdRouteMinAltitude(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                        String position, String value, StdRouteAttributes attributes) {
        super(data, mapViewAdapter, route.getPoint(position,previous),null,attributes);

        this.major=value.substring(0,value.indexOf("."));
        this.minor=value.substring(value.indexOf(".")+1);
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D displayPoint = getDisplayPoint(geoReferencePoint);

        Font boldFont = g2d.getFont().deriveFont(Font.BOLD);
        g2d.setFont(boldFont);
        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(major+" "+minor, g2d);
        Rectangle2D majorBounds = g2d.getFontMetrics().getStringBounds(major, g2d);
        int globalX = (int)(displayPoint.getX()-bounds.getWidth()/2);

        g2d.drawString(major, globalX, (int)(displayPoint.getY()+bounds.getHeight()/2-2));

        int fontSize = boldFont.getSize();
        g2d.setFont(boldFont.deriveFont(Math.round(fontSize*0.6f)));
        g2d.drawString(minor, (int)(globalX+majorBounds.getWidth()+2), (int)(displayPoint.getY()+bounds.getHeight()/2 - fontSize*0.4));

        bounds.setRect(displayPoint.getX(), displayPoint.getY(),bounds.getWidth(),bounds.getHeight());
        return bounds;
    }

    @Override
    public Point2D getEndPoint() {
        return geoReferencePoint;
    }

    @Override
    public boolean contains(Point p) {
        return false;
    }
}
