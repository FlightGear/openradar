package de.knewcleus.fgfs.geodata.geometry;

public interface IGeometryVisitor {
	public void visit(Geometry geometry) throws GeometryException;
	public void visit(GeometryContainer<?> container) throws GeometryException;
	public void visit(LineString linestring) throws GeometryException;
	public void visit(Ring ring) throws GeometryException;
	public void visit(MultiLineString multilinestring) throws GeometryException;
	public void visit(Polygon polygon) throws GeometryException;
	public void visit(NullShape nullshape) throws GeometryException;
	public void visit(Point point) throws GeometryException;
}
