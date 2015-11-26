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

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import de.knewcleus.openradar.gui.setup.AirportData;
import de.knewcleus.openradar.view.groundnet.ParkPos;
import de.knewcleus.openradar.view.groundnet.TaxiWaySegment;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class ParkingPosition extends AViewObject {

    private AirportData data;
    private String activeText;
    private ParkPos parkPos;

    public ParkingPosition(AirportData data, TaxiWaySegment seg, ParkPos parkPos, Font font, Color color, int minScaleText, int maxScaleText) {
        super(font, color, parkPos.getDisplayName(), minScaleText, maxScaleText);
        this.data = data;
        this.activeText = parkPos.getDisplayName();
        this.parkPos = parkPos;

        this.fillPath=true;
//        // try to hide even numbers at a lower level to reduce overlap
//        try {
//            int i = Integer.parseInt(text);
//            if (i % 2 == 0) {
//                this.maxScaleText = 8;
//            }
//        } catch (NumberFormatException e) {}
    }

    @Override
    public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mapViewAdapter) {

        Point2D logPoint = mapViewAdapter.getProjection().toLogical(parkPos.getGeoPoint2D());
        Point2D point = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint, null);

        double x = point.getX()-5;
        double y = point.getY()+5;

        setTextCoordinates(new Point2D.Double(x, y));
        if(data.getRadarObjectFilterState("PPN")) {
            text = activeText;
        } else {
            text = null;
        }

    }
}
