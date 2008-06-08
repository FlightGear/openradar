package de.knewcleus.fgfs.location;

import java.awt.geom.Point2D;


public interface IMapProjection {
	public Point2D forward(Position pos);
	public Position backward(Point2D point);
}
