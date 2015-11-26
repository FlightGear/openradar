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
package de.knewcleus.openradar.view.objects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class TaxiwayLine extends AViewObject {

    private TaxiWaySegment seg;

    public TaxiwayLine(TaxiWaySegment seg,int minScale, int maxScale) {
        super(new Color(177,181,64));
        setMinScalePath(minScale);
        setMaxScalePath(maxScale);
//        if(seg.getBegin().getPaintStyle()==1) {
//            // center line
//            color = Color.yellow;
//        }
//        if(seg.getBegin().getPaintStyle()==4) {
//            // hold position
//            color = Color.white;
//        }
//        if(seg.getBegin().getPaintStyle()==5) {
//            // hold Position
//            color = Color.blue;
//        }
//        if(seg.getBegin().getPaintStyle()==6) {
//            // ILS hold
//            color = Color.red;
//        }
//        if(seg.getBegin().getPaintStyle()==7) {
//            // taxi center line
//            color = Color.yellow;
//        }


        setStroke( new BasicStroke(0.3f) );
        this.seg = seg;
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewerAdapter) {

        boolean isBeginPP = seg.getBegin() instanceof ParkPos;
        boolean isEndPP = seg.getEnd() instanceof ParkPos;

        Point2D logStartPoint = mapViewerAdapter.getProjection().toLogical(seg.getBegin().getGeoPoint2D());
        Point2D startPoint = mapViewerAdapter.getLogicalToDeviceTransform().transform(logStartPoint,null);
        Point2D logEndPoint = mapViewerAdapter.getProjection().toLogical(seg.getEnd().getGeoPoint2D());
        Point2D endPoint = mapViewerAdapter.getLogicalToDeviceTransform().transform(logEndPoint,null);

        if(isBeginPP) {
            startPoint = new Point2D.Double(startPoint.getX()-8*seg.getOrientationX(),startPoint.getY()-8*seg.getOrientationY());
        }
        if(isEndPP) {
            endPoint = new Point2D.Double(endPoint.getX()-8*seg.getOrientationX(),endPoint.getY()-8*seg.getOrientationY());
        }

        path = new Path2D.Double();
        path.append(new Line2D.Double(startPoint, endPoint), false);
    }

}
