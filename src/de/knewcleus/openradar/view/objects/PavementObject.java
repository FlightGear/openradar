/**
 * Copyright (C) 2015-2016 Wolfram Wagner
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
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne
 * die implizite Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General Public
 * License für weitere Details.
 *
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem Programm erhalten haben. Wenn nicht, siehe
 * <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.openradar.view.objects;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import de.knewcleus.fgfs.navaids.Pavement;
import de.knewcleus.fgfs.navaids.PavementNode;
import de.knewcleus.fgfs.navdata.impl.Aerodrome;
import de.knewcleus.openradar.gui.Palette;
import de.knewcleus.openradar.view.Converter2D;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public class PavementObject extends AViewObject {

	// private final Aerodrome aerodrome;
	private final Pavement pavement;

	public PavementObject(Aerodrome aerodrome, Pavement pavement) {
		super(Palette.PAVEMENT_DEFAULT);

		// this.aerodrome = aerodrome;
		this.pavement = pavement;

		setMinScalePath(0);
		setMaxScalePath(500);

		switch (pavement.getSurfaceType()) {
		case Concrete:
			setColor(Palette.PAVEMENT_CONCRETE);
			break;
		case Asphalt:
			setColor(Palette.PAVEMENT_ASPHALT);
			break;
		case Dirt:
			setColor(Palette.PAVEMENT_DIRT);
			break;
		case DryLakebed:
			setColor(Palette.PAVEMENT_DRYLAKEBED);
			break;
		case Gravel:
			setColor(Palette.PAVEMENT_GRAVEL);
			break;
		case SnowIce:
			setColor(Palette.PAVEMENT_SNOWICE);
			break;
		case Transparent:
			setColor(Palette.PAVEMENT_TRANSPARENT);
			break;
		case TurfGrass:
			setColor(Palette.PAVEMENT_TURFGRASS);
			break;
		case Water:
			setColor(Palette.PAVEMENT_WATER);
			break;
		default:
			break;
		}
		fillPath = true;

	}

	public void updateLogicalPosition(IMapViewerAdapter mapViewAdapter) {
		for (PavementNode pn : pavement.getNodes()) {
			pn.updateLogicalPoints(mapViewAdapter);
		}
	}

	@Override
	public void constructPath(Point2D currentDisplayPosition, Point2D newDisplayPosition, IMapViewerAdapter mva) {	
		setMaxScalePath(500);
		path = new Path2D.Double();
		PavementNode start = null;
		for (PavementNode pn : pavement.getNodes()) {
			pn.updateDevicePoints(mva);
			PavementNode end = pn;
			if (start != null) {
				if (start.isBezierNode && end.isBezierNode) {
					path.curveTo(start.devBezierPoint.getX(), start.devBezierPoint.getY(), end.devBezierPoint.getX(), end.devBezierPoint.getY(),
							end.devPoint.getX(), end.devPoint.getY());
				} else {
					path.lineTo(end.devPoint.getX(), end.devPoint.getY());
				}
				// nicer but produces gaps
				// if(!start.isBezierNode && !end.isBezierNode) {
				// path.lineTo(dEnd.getX(),dEnd.getY());
				// } else {
				// if(!start.isBezierNode && end.isBezierNode) {
				// path.curveTo(
				// dStart.getX(),dStart.getY(),
				// dControlEnd.getX(),dControlEnd.getY(),
				// dEnd.getX(),dEnd.getY());
				// } else {
				// if(start.isBezierNode && end.isBezierNode) {
				// path.curveTo(
				// dControlStart.getX(),dControlStart.getY(),
				// dControlEnd.getX(),dControlEnd.getY(),
				// dEnd.getX(),dEnd.getY());
				// }
				// }
				// }
			} else {
				Point2D dStart = Converter2D.getMapDisplayPoint(pn.point, mva);
				path.moveTo(dStart.getX(), dStart.getY());
			}
			if (pn.isEndNode || pn.isCloseLoop) {
				start = null;
			} else {
				start = end;
			}
			if (pn.isCloseLoop) {
				path.closePath();
			}
		}
	}

	@Override
	public synchronized void paint(Graphics2D g2d, IMapViewerAdapter mapViewAdapter) {
		// TODO Auto-generated method stub
		super.paint(g2d, mapViewAdapter);
	}

}
