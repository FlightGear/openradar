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
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.openradar.view.map.IMapViewerAdapter;
/*
 *.center Navaid code or geo coordinates ("lat,lon")
  .radius in miles
  .beginAngle
  .endAngle
  .stroke (optional if differs from normal line) alternatives: "dashed","dots"
  .lineWidth (optional)

 * @author wolfram
 *
 */
public class StdRouteBow extends AStdRouteElement {

    private final Point2D geoEndPoint;
    private final Point2D geoControlPoint;
    
    public StdRouteBow(StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous, String geoStartPoint, String geoEndPoint, String geoControlPoint,String stroke, String lineWidth) {
                                                                                                
        super(mapViewAdapter, geoStartPoint);
        this.geoEndPoint = geoEndPoint;
        this.geoControlPoint = geoControlPoint;
    }
    
    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D bowStartPoint = getDisplayPoint(geoReferencePoint);
        Point2D bowControlPoint = getDisplayPoint(geoControlPoint);
        Point2D bowEndPoint = getDisplayPoint(geoEndPoint);

        
        Path2D path = new Path2D.Double(); 
        path.append(new QuadCurve2D.Double(bowStartPoint.getX(),bowStartPoint.getY(),bowControlPoint.getX(),bowControlPoint.getY(),bowEndPoint.getX(),bowEndPoint.getY()),false);
        g2d.draw(path);
        return path.getBounds2D();
    }

    @Override
    public Point2D getEndPoint() {
        // TODO Auto-generated method stub
        return null;
    }

}
