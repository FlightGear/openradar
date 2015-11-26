/**
 * Copyright (C) 2013,2015 Wolfram Wagner
 *
 * This file is part of OpenRadar.
 *
 * OpenRadar is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with OpenRadar. If not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Diese Datei ist Teil von OpenRadar.
 *
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU General Public License, wie von der Free
 * Software Foundation, Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 *
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHELEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.stdroutes;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

/**
 *
 * @author Wolfram Wagner
 *
 */
public class StdRouteMultipointLine extends AStdRouteElement {

    private final List<Point2D> geoPoints = new ArrayList<Point2D>();
    private final Point2D geoEndPoint;
    private final boolean close;

    public StdRouteMultipointLine(AirportData data, StdRoute route, IMapViewerAdapter mapViewAdapter, AStdRouteElement previous, List<String> points, String close,
            StdRouteAttributes attributes) {
        super(data, mapViewAdapter, route.getPoint(points.get(0), previous), null,attributes);

        for (String point : points) {
            Point2D geoPoint = route.getPoint(point, previous);
            if (point == null) {
                throw new IllegalArgumentException("MulipointLine: Point " + point + " not found!");
            }
            geoPoints.add(geoPoint);
        }
        geoEndPoint = geoPoints.get(geoPoints.size()-1);
        this.close = !"false".equals(close);
    }

    @Override
    public Rectangle2D paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {

        Point2D lastDisplayPoint = null;
        Path2D path = new Path2D.Double();

        for (Point2D geoPoint : geoPoints) {
            Point2D displayPoint = getDisplayPoint(geoPoint);
            if (lastDisplayPoint != null) {
                path.append(new Line2D.Double(lastDisplayPoint, displayPoint), false);
            }
            lastDisplayPoint=displayPoint;
        }

        if (close) {
            path.closePath();
        }

        g2d.draw(path);

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
