package de.knewcleus.fgfs.geodata.geometry;

import java.util.List;

public class LineString extends GeometryContainer<Point> {
	public List<Point> getPoints() {
		return getContainedGeometry();
	}
	
	@Override
	public void accept(IGeometryVisitor visitor) throws GeometryException {
		visitor.visit(this);
	}
}