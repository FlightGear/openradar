package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;


public interface IDeviceTransformation {
	public Point2D toDevice(Position pos);
	public Position fromDevice(Point2D point);
	
	public Point2D toDeviceRelative(Vector3D dimension);
	public Position fromDeviceRelative(Point2D dimension);
}
