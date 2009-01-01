package de.knewcleus.openradar.view;

import java.awt.geom.Point2D;

public interface IPickable {
	/**
	 * Return true iff the given point hits this object.
	 * @param devicePoint	The point in device coordinates
	 * @return true if and only if the point hits this object.
	 */
	public boolean contains(Point2D devicePoint);
}
