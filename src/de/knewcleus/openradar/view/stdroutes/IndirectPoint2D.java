/**
 * Copyright (C) 2014-2015 Wolfram Wagner 
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

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

/**
 * Author: Wolfram Wagner
 * Contributions: Andreas Vogel
 */
public class IndirectPoint2D extends Point2D {

    private Point2D geoOrigPoint;
    private final double distance;
    private final double direction;
    private Point2D geoOrigPoint2;
    private final double direction2;
    private final IMapViewerAdapter viewerAdapter;
    
    public IndirectPoint2D(IMapViewerAdapter viewerAdapter, Point2D geoOrigPoint, double direction, double distance) {
        this.viewerAdapter=viewerAdapter;
        this.geoOrigPoint = geoOrigPoint;
        this.distance=distance;
        this.direction = direction;
        this.geoOrigPoint2 = null;
        this.direction2 = 0;
    }

    public IndirectPoint2D(IMapViewerAdapter viewerAdapter, Point2D geoOrigPoint1, double direction1, Point2D geoOrigPoint2, double direction2) {
        this.viewerAdapter = viewerAdapter;
        this.geoOrigPoint = geoOrigPoint1;
        this.distance = 0;
        this.direction = direction1;
        this.geoOrigPoint2 = geoOrigPoint2;
        this.direction2 = direction2;
    }

    private Point2D CalculatedPoint () {
        Point2D logPoint = viewerAdapter.getProjection().toLogical(geoOrigPoint);
        Point2D devicePoint = viewerAdapter.getLogicalToDeviceTransform().transform(logPoint, null);
        Point2D newDevPoint;
        if (geoOrigPoint2 == null) {
            newDevPoint = Converter2D.getMapDisplayPoint(devicePoint, direction, Converter2D.getFeetToDots(distance * Units.NM /Units.FT, viewerAdapter));
        } else {
            logPoint = viewerAdapter.getProjection().toLogical(geoOrigPoint2);
            Point2D devicePoint2 = viewerAdapter.getLogicalToDeviceTransform().transform(logPoint, null);
            newDevPoint = Converter2D.getMapDisplayPointIntersect(devicePoint, direction, devicePoint2, direction2);
        }
        Point2D newLogPoint = viewerAdapter.getDeviceToLogicalTransform().transform(newDevPoint, null);
        return viewerAdapter.getProjection().toGeographical(newLogPoint);
    }
    
    @Override
    public double getX() {
        return CalculatedPoint().getX();
    }

    @Override
    public double getY() {
        return CalculatedPoint().getY();
    }

    @Override
    public void setLocation(double x, double y) {
        throw new IllegalStateException("This class is immutable");
    }
}
