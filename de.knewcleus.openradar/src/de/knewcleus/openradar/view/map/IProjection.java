package de.knewcleus.openradar.view.map;

import java.awt.geom.Point2D;

/**
 * A projection describes the transformation between logical and geographical coordinates.
 * 
 * Geographic coordinates are expressed in degrees of latitude and longitude.
 * 
 * Logical coordinates are expressed in a unit of length.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IProjection {
	public Point2D toLogical(Point2D geographical);
	public Point2D toGeographical(Point2D logical);
}
