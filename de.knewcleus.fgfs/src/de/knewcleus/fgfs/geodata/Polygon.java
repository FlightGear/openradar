package de.knewcleus.fgfs.geodata;

import java.util.List;

public class Polygon extends GeometryContainer<Ring> {
	protected Ring outerRing=null;
	
	@Override
	public void add(Ring geometry) {
		super.add(geometry);
		if (outerRing==null) {
			outerRing=geometry;
		}
	}
	
	public Ring getOuterRing() {
		return outerRing;
	}
	
	public List<Ring> getRings() {
		return getContainedGeometry();
	}
	
	public double getArea() {
		double area=0.0;
		for (Ring ring: getRings()) {
			area+=ring.getEnclosedArea();
		}
		
		return area;
	}
}
