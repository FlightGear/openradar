/**
 * 
 */
package de.knewcleus.fgfs.navdata.test;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.fgfs.navdata.INavDatumFilter;
import de.knewcleus.fgfs.navdata.model.IAirwaySegment;
import de.knewcleus.fgfs.navdata.model.IIntersection;
import de.knewcleus.fgfs.navdata.model.INavDatum;
import de.knewcleus.fgfs.navdata.model.INavPoint;

public class SpatialFilter implements INavDatumFilter<INavDatum> {
	protected final Rectangle2D bounds;
	
	public SpatialFilter(Rectangle2D bounds) {
		this.bounds = bounds;
	}
	
	@Override
	public boolean allow(INavDatum datum) {
		if (datum instanceof INavPoint) {
			final INavPoint point = (INavPoint) datum;
			return bounds.contains(point.getGeographicPosition());
		} else if (datum instanceof IAirwaySegment) {
			final IAirwaySegment segment = (IAirwaySegment) datum;
			final IIntersection start, end;
			start = segment.getStartPoint();
			end = segment.getEndPoint();
			final Line2D line = new Line2D.Double(start.getGeographicPosition(), end.getGeographicPosition());
			return line.intersects(bounds);
		} 
		return true;
	}
}