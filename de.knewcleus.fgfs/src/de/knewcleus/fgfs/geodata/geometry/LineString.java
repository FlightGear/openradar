package de.knewcleus.fgfs.geodata.geometry;

import java.util.List;

import de.knewcleus.fgfs.geodata.GeodataException;

public class LineString extends GeometryContainer<Point> {
	public List<Point> getPoints() {
		return getContainedGeometry();
	}
	
	@Override
	public void accept(IGeometryVisitor visitor) throws GeodataException {
		visitor.visit(this);
	}
}