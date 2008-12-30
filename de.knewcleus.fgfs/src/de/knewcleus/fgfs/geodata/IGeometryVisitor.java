package de.knewcleus.fgfs.geodata;

public interface IGeometryVisitor {
	public void visit(Geometry geometry);
	public void visit(GeometryContainer<?> container);
	public void visit(LineString linestring);
	public void visit(Ring ring);
	public void visit(MultiLineString multilinestring);
	public void visit(Polygon polygon);
	public void visit(NullShape nullshape);
	public void visit(Point point);
}
