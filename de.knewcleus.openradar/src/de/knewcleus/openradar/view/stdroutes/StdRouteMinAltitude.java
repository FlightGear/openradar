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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

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

    private final String font;
    private final Float fontSize;
    private final String major;
    private final String minor;

    public StdRouteMinAltitude(StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous,
                        String position, String value, String font, String fontSize,
                        String color) {
        super(mapViewAdapter, route.getPoint(position,previous),null,null,null,color);

        this.font = font!=null ? font : "Arial";
        this.fontSize = fontSize !=null ? Float.parseFloat(fontSize) : 18;
        this.major=value.substring(0,value.indexOf("."));
        this.minor=value.substring(value.indexOf(".")+1);
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        if(color!=null) {
            g2d.setColor(color);
        } else {
            g2d.setColor(new Color(114,162,210));
        }

        Point2D displayPoint = getDisplayPoint(geoReferencePoint);

        g2d.setFont(new Font(font,Font.BOLD,Math.round(fontSize)));
        Rectangle2D bounds = g2d.getFontMetrics().getStringBounds(major+" "+minor, g2d);
        Rectangle2D majorBounds = g2d.getFontMetrics().getStringBounds(major, g2d);
        int globalX = (int)(displayPoint.getX()-bounds.getWidth()/2);

        g2d.drawString(major, globalX, (int)(displayPoint.getY()+bounds.getHeight()/2-2));

        g2d.setFont(new Font(font,Font.BOLD,Math.round(fontSize*0.6f)));
        g2d.drawString(minor, (int)(globalX+majorBounds.getWidth()+2), (int)(displayPoint.getY()+bounds.getHeight()/2 - fontSize*0.4));

        bounds.setRect(displayPoint.getX(), displayPoint.getY(),bounds.getWidth(),bounds.getHeight());
        return bounds;
    }

    @Override
    public Point2D getEndPoint() {
        return geoReferencePoint;
    }

}
