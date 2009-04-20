package de.knewcleus.openradar.sector.impl;

import de.knewcleus.openradar.sector.Bounds;
import de.knewcleus.openradar.sector.FileLayer;
import de.knewcleus.openradar.sector.GeographicPosition;
import de.knewcleus.openradar.sector.Polygon;
import de.knewcleus.openradar.sector.PolygonLayer;
import de.knewcleus.openradar.sector.Sector;
import de.knewcleus.openradar.sector.SectorFactory;
import de.knewcleus.openradar.sector.Topology;

public class SectorFactoryImpl implements SectorFactory {
	@Override
	public Sector createSector() {
		return new SectorImpl();
	}
	
	@Override
	public Bounds createBounds() {
		return new BoundsImpl();
	}
	
	@Override
	public Topology createTopology() {
		return new TopologyImpl();
	}
	
	@Override
	public FileLayer createFileLayer() {
		return new FileLayerImpl();
	}
	
	@Override
	public PolygonLayer createPolygonLayer() {
		return new PolygonLayerImpl();
	}
	
	@Override
	public Polygon createPolygon() {
		return new PolygonImpl();
	}
	
	@Override
	public GeographicPosition createGeographicPosition() {
		return new GeographicPositionImpl();
	}
}
