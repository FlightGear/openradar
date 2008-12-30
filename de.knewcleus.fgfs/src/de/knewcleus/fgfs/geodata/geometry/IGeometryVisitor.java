package de.knewcleus.fgfs.geodata.geometry;

import de.knewcleus.fgfs.geodata.GeodataException;

public interface IGeometryVisitor {
	public void visit(Geometry geometry) throws GeodataException;
	public void visit(GeometryContainer<?> container) throws GeodataException;
	public void visit(LineString linestring) throws GeodataException;
	public void visit(Ring ring) throws GeodataException;
	public void visit(MultiLineString multilinestring) throws GeodataException;
	public void visit(Polygon polygon) throws GeodataException;
	public void visit(NullShape nullshape) throws GeodataException;
	public void visit(Point point) throws GeodataException;
}
