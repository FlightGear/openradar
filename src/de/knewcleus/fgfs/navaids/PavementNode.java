/**
 * Copyright (C) 2015 Wolfram Wagner
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
package de.knewcleus.fgfs.navaids;

import java.awt.geom.Point2D;

import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class PavementNode {

    public volatile boolean isEndNode;
    public final boolean isCloseLoop;
    public final boolean isBezierNode;

    public final String rowCode;
    public final Point2D point;
    public final Point2D bezierPoint;
    private volatile Point2D logPoint;
    private volatile Point2D logBezierPoint;
    public volatile Point2D devPoint;
    public volatile Point2D devBezierPoint;


    public PavementNode(String[] def) {
        isCloseLoop = def[0].equals("113") || def[0].equals("114") ;
        isEndNode = def[0].equals("113") || def[0].equals("114") || def[0].equals("115") || def[0].equals("116");
        isBezierNode = def[0].equals("112") || def[0].equals("114") || def[0].equals("116");

        rowCode=def[0];
        point = new Point2D.Double(Double.parseDouble(def[2]),Double.parseDouble(def[1]));
        if(isBezierNode) {
            bezierPoint = new Point2D.Double(Double.parseDouble(def[4]),Double.parseDouble(def[3]));
        } else {
            bezierPoint=null;
        }
    }


	public void updateLogicalPoints(IMapViewerAdapter mapViewAdapter) {
		if(point!=null) {
			logPoint = mapViewAdapter.getProjection().toLogical(point);
			if(bezierPoint!=null) {
				logBezierPoint = mapViewAdapter.getProjection().toLogical(bezierPoint);
			}
		}
	}

	public void updateDevicePoints(IMapViewerAdapter mapViewAdapter) {
		if(logPoint == null) {
			updateLogicalPoints(mapViewAdapter);
		}

		if(logPoint!=null) {
			devPoint = mapViewAdapter.getLogicalToDeviceTransform().transform(logPoint,devPoint);
			if(logBezierPoint!=null) {
				devBezierPoint = mapViewAdapter.getLogicalToDeviceTransform().transform(logBezierPoint,devBezierPoint);
			}
		}
	}
}
