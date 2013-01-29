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
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class StdRouteEllipse extends AStdRouteElement {

    private final double inboundHeading;
    private final boolean right;
    
    public StdRouteEllipse(IMapViewerAdapter mapViewAdapter, Point2D geoReferencPoint, double inboundHeading, boolean right, int strikeWidth) {
        
        super(mapViewAdapter, geoReferencPoint);
        this.inboundHeading = inboundHeading;
        this.right = right;
    }
    
    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        double width =  Converter2D.getFeetToDots(2*220/60*Units.NM/Units.FT, mapViewAdapter); // 2min * 220 mph/60h/min
        double height = width / 2 ;
        
        Point2D lineEndPoint1 = getDisplayPoint(geoReferencePoint);
        Point2D lineStartPoint1 = Converter2D.getMapDisplayPoint(lineEndPoint1, inboundHeading+180, width);
        Point2D lineStartPoint2 = Converter2D.getMapDisplayPoint(lineEndPoint1, right ?inboundHeading-90:inboundHeading+90, height);;
        Point2D lineEndPoint2 = Converter2D.getMapDisplayPoint(lineStartPoint2, inboundHeading+180, width);
        Point2D bowControlPoint1 = Converter2D.getMapDisplayPoint(new Point2D.Double((lineEndPoint1.getX()+lineStartPoint2.getX())/2, (lineEndPoint1.getY()+lineStartPoint2.getY())/2),inboundHeading,width/2);
        Point2D bowControlPoint2 = Converter2D.getMapDisplayPoint(new Point2D.Double((lineEndPoint2.getX()+lineStartPoint1.getX())/2, (lineEndPoint2.getY()+lineStartPoint1.getY())/2),inboundHeading+180,width/2);

        
        Path2D path = new Path2D.Double(); 
        path.append(new QuadCurve2D.Double(lineEndPoint1.getX(),lineEndPoint1.getY(),bowControlPoint1.getX(),bowControlPoint1.getY(),lineStartPoint2.getX(),lineStartPoint2.getY()),false);
        path.append(new Line2D.Double(lineStartPoint2,lineEndPoint2),true);
        path.append(new QuadCurve2D.Double(lineEndPoint2.getX(),lineEndPoint2.getY(),bowControlPoint2.getX(),bowControlPoint2.getY(),lineStartPoint1.getX(),lineStartPoint1.getY()),true);
        path.append(new Line2D.Double(lineStartPoint1,lineEndPoint1),true);
        path.closePath();
        g2d.draw(path);
        return path.getBounds2D();
    }
}
