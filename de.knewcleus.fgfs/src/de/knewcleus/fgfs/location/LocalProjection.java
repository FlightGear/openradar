package de.knewcleus.fgfs.location;

import de.knewcleus.fgfs.Units;

public class LocalProjection implements ICoordinateTransformation {
	protected Position center;
	protected double lonwidth;
	
	public LocalProjection(Position center) {
		this.center=center;
		lonwidth=60.0*Units.NM*Math.cos(Math.toRadians(center.getY()));
	}

	public Position forward(Position pos) {
		double x=(pos.getX()-center.getX())*lonwidth;
		double y=(pos.getY()-center.getY())*60.0*Units.NM;
		return new Position(x,y,0.0);
	}

	public Position backward(Position pos) {
		double lat=pos.getY()/60.0/Units.NM+center.getX();
		double lon=pos.getX()/lonwidth+center.getY();
		return new Position(lon,lat,0.0);
	}

}
