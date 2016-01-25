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
 * GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.objects;

import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiPoint;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class HoldPoint extends AViewObject {

//    private TaxiWaySegment seg;
    private TaxiPoint taxiPoint;
    private double radius;
    
    public HoldPoint(TaxiWaySegment seg, TaxiPoint taxiPoint, int minScalePath, int maxScalePath) {
        super(Color.white);
//      this.seg=seg;
        this.taxiPoint = taxiPoint;
        fillPath=true;
        this.minScalePath = minScalePath;
        this.maxScalePath = maxScalePath;
        if(taxiPoint instanceof ParkPos) {
            color = new Color(177,181,64);
            radius = 2;
        } else {
            color = Color.white;
            radius = 2;
        }
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {

        Point2D logPoint = mapViewAdapter.getProjection().toLogical(taxiPoint.getGeoPoint2D());
        Point2D point = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint,null);

        path = new Path2D.Double();
        path.append(new Ellipse2D.Double(point.getX()-radius, point.getY()-radius, 2*radius, 2*radius), false);

    }
}