package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.Units;

public class LocalSphericalProjection implements IMapProjection {
	protected Position center;
	protected double lonwidth;
	
	public LocalSphericalProjection(Position center) {
		this.center=center;
		lonwidth=60.0*Units.NM*Math.cos(Math.toRadians(center.getY()));
	}

	@Override
	public Point2D forward(Position pos) {
		double x=(pos.getX()-center.getX())*lonwidth;
		double y=(pos.getY()-center.getY())*60.0*Units.NM;
		return new Point2D.Double(x, y);
	}

	@Override
	public Position backward(Point2D pos) {
		double lat=pos.getY()/60.0/Units.NM+center.getX();
		double lon=pos.getX()/lonwidth+center.getY();
		return new Position(lon,lat,0.0);
	}

}
