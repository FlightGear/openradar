package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;

public final class Position extends Vector3D {
	public Position() {
	}
	
	public Position(double x, double y, double z) {
		super(x,y,z);
	}
	
	public Position(Position original) {
		super(original);
	}
	
	public void translate(double dx, double dy, double dz) {
		x+=dx;
		y+=dy;
		z+=dz;
	}
	
	public void translate(Vector3D delta) {
		x+=delta.x;
		y+=delta.y;
		z+=delta.z;
	}
	
	public Point2D toPoint2D() {
		return new Point2D.Double(x,y);
	}
}
