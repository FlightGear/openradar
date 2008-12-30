package de.knewcleus.openradar.map;

import static java.lang.Math.cos;
import static java.lang.Math.toRadians;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;

public class LocalSphericalProjection implements IProjection {
	protected final Point2D center;
	protected final static double latwidth=60.0*Units.NM;
	protected final double lonwidth;
	
	public LocalSphericalProjection(Point2D center) {
		this.center = center;
		lonwidth = cos(toRadians(center.getY()))*latwidth;
	}

	@Override
	public Point2D toGeographical(Point2D logical) {
		final double lon, lat;
		lon = center.getX() + logical.getX() / lonwidth;
		lat = center.getY() + logical.getY() / latwidth;
		return new Point2D.Double(lon, lat);
	}

	@Override
	public Point2D toLogical(Point2D geographical) {
		final double x, y;
		x = (geographical.getX() - center.getX()) * lonwidth;
		y = (geographical.getY() - center.getY()) * latwidth;
		return new Point2D.Double(x, y);
	}

}
