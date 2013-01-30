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
package de.knewcleus.openradar.view;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public abstract class Converter2D {

    /**
     * This method returns a point in a direction and a distance. It assumes an coordinate system with origin in the top left, and y increasing downwards,
     * like our screen coordinates and a heading expressed in relation to our north, growing clockwise. 
     * 
     * @param origin
     * @param heading
     * @param distance
     * @return
     */
    public static Point2D getMapDisplayPoint(Point2D origin, double heading, double distance) {
        double headingCorrected = (heading-90d)*2*Math.PI/360d ;
        
        double x = distance * Math.cos(headingCorrected);
        double y = distance * Math.sin(headingCorrected);

        return addPoints(origin, new Point2D.Double(x,y));
    }
    
    public Point2D getGeographicPoint(Point2D origin, double heading, double distance) {
        return null;
    }

    public static Point2D addPoints(Point2D point1, Point2D point2) {
        return new Point2D.Double(point1.getX()+point2.getX(),point1.getY()+point2.getY());
    }
    
    
    public static double getFeetToDots(double distance, IMapViewerAdapter mapViewerAdapter) {
        double scale = mapViewerAdapter.getLogicalScale();
        scale = scale == 0 ? 1 : scale;
        return distance / 6076d / scale * 1900d; // the last number is the global correction factor for distances
    }
    
    public double getDistanceMiles(Point2D geoPoint1, Point2D geoPoint2) {
        return 0d;
    }

    public static double getMilesPerDot(IMapViewerAdapter mapViewerAdapter) {
        return 1d/getFeetToDots(Units.NM/Units.FT, mapViewerAdapter);
    }

    public static double normalizeAngle(double d) {
        while(d>=360) {
            d = d-360;
        }
        while(d<0) {
            d = d+360;
        }
        return d;
    }

    public static double getDirection (Point2D point1, Point2D point2) {
        double dx = point1.getX()-point2.getX();
        double dy = point2.getY()-point1.getY();

        double distance = (double)point2.distance(point1);
        Long angle = null;
        if(distance!=0) {
            if(dx>0 && dy>0) angle = Math.round(Math.asin(dx/distance)/2d/Math.PI*360d); 
            if(dx>0 && dy<0) angle = 180-Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
            if(dx<0 && dy<0) angle = 180+-1*Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
            if(dx<0 && dy>0) angle = 360+Math.round(Math.asin(dx/distance)/2d/Math.PI*360d);
        }
        long degrees = angle!=null ? ( angle<0 ? angle+360 : angle) : -1;
        
        return degrees;
    }
}
