package de.knewcleus.openradar.sector.impl;

import java.util.ArrayList;
import java.util.List;

import de.knewcleus.openradar.sector.GeographicPosition;
import de.knewcleus.openradar.sector.Polygon;

public class PolygonImpl implements Polygon {
	protected List<GeographicPosition> point = new ArrayList<GeographicPosition>();

	@Override
	public List<GeographicPosition> getPoint() {
		return point;
	}
}
