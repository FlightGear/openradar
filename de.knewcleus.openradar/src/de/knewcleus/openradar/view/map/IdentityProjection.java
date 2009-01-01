package de.knewcleus.openradar.view.map;

import java.awt.geom.Point2D;


public class IdentityProjection implements IProjection {

	@Override
	public Point2D toGeographical(Point2D logical) {
		return logical;
	}

	@Override
	public Point2D toLogical(Point2D geographical) {
		return geographical;
	}

}
