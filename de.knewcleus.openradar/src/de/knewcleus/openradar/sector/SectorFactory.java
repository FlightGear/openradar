package de.knewcleus.openradar.sector;

import de.knewcleus.openradar.sector.impl.SectorFactoryImpl;

public interface SectorFactory {
	public final static SectorFactory instance = new SectorFactoryImpl();
	
	public Sector createSector();
	public Bounds createBounds();
	public Topology createTopology();
	public FileLayer createFileLayer();
	public PolygonLayer createPolygonLayer();
	public Polygon createPolygon();
	public GeographicPosition createGeographicPosition();
}
