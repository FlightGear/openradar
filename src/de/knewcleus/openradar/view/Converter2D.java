/**
 * Copyright (C) 2012,2016 Wolfram Wagner
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
package de.knewcleus.openradar.view;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.view.map.IMapViewerAdapter;

public abstract class Converter2D {

	/**
	 * This method returns a point in a direction and a distance. It assumes an
	 * coordinate system with origin in the top left, and y increasing
	 * downwards, like our screen coordinates and a heading expressed in
	 * relation to our north, growing clockwise.
	 *
	 * Author: Wolfram Wagner
	 * Contributions: Andreas Vogel
	 * 
	 * @param origin
	 * @param heading
	 * @param distance
	 * @return
	 */
	public static Point2D getMapDisplayPoint(Point2D origin, double heading, double distance) {
		double headingCorrected = (heading - 90d) * 2 * Math.PI / 360d;

		double x = distance * Math.cos(headingCorrected);
		double y = distance * Math.sin(headingCorrected);

		return addPoints(origin, new Point2D.Double(x, y));
	}

    public static Point2D getVectorFromHeading(double heading) {
        double headingCorrected = (heading-90d)*2*Math.PI/360d ;
        return new Point2D.Double(Math.cos(headingCorrected), Math.sin(headingCorrected));
    }

    public static Point2D getMapDisplayPointIntersect(Point2D origin1, double heading1, Point2D origin2, double heading2) {
    	Point2D vector1 = getVectorFromHeading (heading1);
    	Point2D vector2 = getVectorFromHeading (heading2);
    	double dox = origin2.getX() - origin1.getX();
    	double doy = origin2.getY() - origin1.getY();
    	double v1x = vector1.getX();
    	double v1y = vector1.getY();
    	double v2x = vector2.getX();
    	double v2y = vector2.getY();
    	double qx = dox * v1x + doy * v1y;
    	double qy = dox * (-v1y) + doy * v1x;
    	double sx = v2x * v1x + v2y * v1y;
    	double sy = v2x * (-v1y) + v2y * v1x;
    	double d = qx - qy * sx / sy;
    	return new Point2D.Double(origin1.getX() + d * v1x, origin1.getY() + d * v1y);
    }
    
	public Point2D getGeographicPoint(Point2D origin, double heading, double distance) {
		return null;
	}

	public static Point2D addPoints(Point2D point1, Point2D point2) {
		return new Point2D.Double(point1.getX() + point2.getX(), point1.getY() + point2.getY());
	}

	public static Point2D midPoint(Point2D point1, Point2D point2) {
		return new Point2D.Double((point1.getX() + point2.getX()) / 2, (point1.getY() + point2.getY()) / 2);
	}

	public static double getFeetToDots(double distance, IMapViewerAdapter mapViewerAdapter) {
		double scale = mapViewerAdapter.getLogicalScale();
		scale = scale == 0 ? 1 : scale;
		return distance / 6076d / scale * 1850d; // the last number is the global correction factor for distances
	}

	public double getDistanceMiles(Point2D geoPoint1, Point2D geoPoint2) {
		return 0d;
	}

	public static double getMilesPerDot(IMapViewerAdapter mapViewerAdapter) {
		return 1d / getFeetToDots(Units.NM / Units.FT, mapViewerAdapter);
	}

	public static double normalizeAngle(double d) {
		// TODO: use mod operator %
		while (d > 360) {
			d = d - 360;
		}
		while (d < 0) {
			d = d + 360;
		}
		return d;
	}

	public static double getDirection(Point2D point1, Point2D point2) {
		double dx = point2.getX() - point1.getX();
		double dy = -1 * (point2.getY() - point1.getY());
		return getDirection(dx, dy);
	}

	public static double getDirection(double dx, double dy) {

		double distance = Math.sqrt(dx * dx + dy * dy);
		Long angle = null;
		if (distance != 0) {
			if (dx >= 0 && dy >= 0)
				angle = Math.round(Math.asin(dx / distance) / 2d / Math.PI * 360d);
			if (dx >= 0 && dy <= 0)
				angle = 180 - Math.round(Math.asin(dx / distance) / 2d / Math.PI * 360d);
			if (dx <= 0 && dy <= 0)
				angle = 180 + -1 * Math.round(Math.asin(dx / distance) / 2d / Math.PI * 360d);
			if (dx <= 0 && dy >= 0)
				angle = 360 + Math.round(Math.asin(dx / distance) / 2d / Math.PI * 360d);
		}
		// TODO: use normalizeAngle
		long degrees = angle != null ? (angle < 0 ? angle + 360 : angle) : -1;

		return degrees;
	}

	public static int toDisplayAngle(long l) {
		return l == 0 ? 360 : (int) l;
	}

	public static Point2D getMapDisplayPoint(Point2D geographical, IMapViewerAdapter mva) {
		Point2D logical = mva.getProjection().toLogical(geographical);
		return mva.getLogicalToDeviceTransform().transform(logical, null);
	}

}
