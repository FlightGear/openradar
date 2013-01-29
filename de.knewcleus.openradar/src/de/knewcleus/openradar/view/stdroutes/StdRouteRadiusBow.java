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

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class StdRouteRadiusBow extends AStdRouteElement {

    private final double radius;
    private final double beginAngle;
    private final double endAngle;
    
    public StdRouteRadiusBow(IMapViewerAdapter mapViewAdapter, Point2D geoReferencPoint, double radius, double beginAngle, double endAngle, int strikeWidth) {

        super(mapViewAdapter, geoReferencPoint);
        this.radius = radius;
        this.beginAngle = beginAngle;
        this.endAngle = endAngle;
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        double radiusDots = Converter2D.getFeetToDots(radius * Units.NM / Units.FT, mapViewAdapter);

        Point2D bowStartPoint = Converter2D.getMapDisplayPoint(getDisplayPoint(geoReferencePoint), beginAngle, radiusDots);
        Point2D bowControlPoint = Converter2D.getMapDisplayPoint(getDisplayPoint(geoReferencePoint), (beginAngle + endAngle) / 2, radiusDots * 1.2);
        Point2D bowEndPoint = Converter2D.getMapDisplayPoint(getDisplayPoint(geoReferencePoint), endAngle, radiusDots);

        Path2D path = new Path2D.Double();
        path.append(new QuadCurve2D.Double(bowStartPoint.getX(),bowStartPoint.getY(),bowControlPoint.getX(),bowControlPoint.getY(),bowEndPoint.getX(),bowEndPoint.getY()),false);
        return path.getBounds2D();
    }
}