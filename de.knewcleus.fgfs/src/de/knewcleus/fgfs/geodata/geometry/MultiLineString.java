package de.knewcleus.fgfs.geodata.geometry;

import de.knewcleus.fgfs.geodata.GeodataException;

public class MultiLineString extends GeometryContainer<LineString> {
	@Override
	public void accept(IGeometryVisitor visitor) throws GeodataException {
		visitor.visit(this);
	}
}
