package de.knewcleus.openradar.sector.impl;

import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.sector.Polygon;
import de.knewcleus.openradar.sector.PolygonLayer;

public class PolygonLayerImpl implements PolygonLayer {
	protected List<Polygon> polygon=new ArrayList<Polygon>();

	@Override
	public List<Polygon> getPolygon() {
		return polygon;
	}
}
