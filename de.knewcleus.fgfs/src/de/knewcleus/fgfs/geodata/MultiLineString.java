package de.knewcleus.fgfs.geodata;

public class MultiLineString extends GeometryContainer<LineString> {
	@Override
	public void accept(IGeometryVisitor visitor) throws GeometryException {
		visitor.visit(this);
	}
}
