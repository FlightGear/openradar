package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;

public final class Position extends Vector3D {
	public Position() {
	}
	
	public Position(double x, double y, double z) {
		super(x,y,z);
	}
	
	public Position(Vector3D original) {
		super(original);
	}
	
	@Override
	public Position add(Vector3D b) {
		return new Position(super.add(b));
	}
	
	public Point2D toPoint2D() {
		return new Point2D.Double(x,y);
	}
}
