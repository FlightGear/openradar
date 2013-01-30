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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
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
    private final Double beginOffSet;
    private final Double endOffSet;
    private final Stroke stroke;
    private final Float lineWidth;
    private final boolean arrows;
    
    public StdRouteLine(StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous, 
                        String begin, String end, String angle, String length, String beginOffset, String endOffset, 
                        String stroke, String lineWidth, String arrows) {
        super(mapViewAdapter, route.getPoint(begin,previous));
        
        this.geoStartPoint = route.getPoint(begin,previous);
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
        this.angle = angle !=null ? Double.parseDouble(angle) : null;
        this.length = length !=null ? Double.parseDouble(length) : null;
        this.beginOffSet = beginOffset !=null ? Double.parseDouble(beginOffset) : 0 ;
        this.endOffSet = endOffset !=null ? Double.parseDouble(endOffset) : 0 ;
        this.lineWidth = lineWidth !=null ? Float.parseFloat(lineWidth) : 1 ;
        if(stroke!=null) {
            if("dashed".equalsIgnoreCase(stroke)) {
                float[] dashPattern = { 10, 10 };
                this.stroke = new BasicStroke(this.lineWidth, BasicStroke.CAP_BUTT,
                                              BasicStroke.JOIN_MITER, 10,
                                              dashPattern, 0);                
            } else if("dots".equalsIgnoreCase(stroke)) {
                    float[] dashPattern = { this.lineWidth, 2 * this.lineWidth};
                    this.stroke = new BasicStroke(this.lineWidth, BasicStroke.CAP_BUTT,
                                                  BasicStroke.JOIN_MITER, 10,
                                                  dashPattern, 0);                
            } else {
                this.stroke = new BasicStroke(this.lineWidth);
            }
        } else {
            this.stroke = new BasicStroke(this.lineWidth);
        }
        this.arrows = "true".equalsIgnoreCase(arrows);
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
        
        Point2D startPoint = getDisplayPoint(geoStartPoint);
        Point2D endPoint = null;
        if(geoEndPoint!=null) {
            endPoint = getDisplayPoint(geoEndPoint);
        } else {
            endPoint = Converter2D.getMapDisplayPoint(startPoint, angle, Converter2D.getFeetToDots(length*Units.NM/Units.FT, mapViewAdapter));
        }

        if(beginOffSet!=null) {
            angle = angle!=null ? angle : Converter2D.getDirection(startPoint, endPoint); 
            startPoint = Converter2D.getMapDisplayPoint(startPoint, angle, Converter2D.getFeetToDots(beginOffSet*Units.NM/Units.FT, mapViewAdapter));
        }
        
        if(endOffSet!=null) {
            angle = angle!=null ? angle : (long)Converter2D.getDirection(startPoint, endPoint); ; 
            startPoint = Converter2D.getMapDisplayPoint(endPoint, angle-180, Converter2D.getFeetToDots(endOffSet*Units.NM/Units.FT, mapViewAdapter));
        }
        
        Shape shape = new Line2D.Double(startPoint, endPoint);
        Stroke origStroke = g2d.getStroke();
        g2d.setStroke(stroke);
        g2d.draw(shape);
        g2d.setStroke(origStroke);
        
        // todo paint arrows
        
        return shape.getBounds2D();
    }

    @Override
    public Point2D getEndPoint() {
        return null;
    }

}
