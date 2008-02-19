package de.knewcleus.radar.sector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Polygon implements Iterable<PolygonContour> {
	protected final List<PolygonContour> contours=new ArrayList<PolygonContour>();
	
	public void addContour(PolygonContour contour) {
		contours.add(contour);
	}
	
	public List<PolygonContour> getContours() {
		return contours;
	}
	
	public Iterator<PolygonContour> iterator() {
		return contours.iterator();
	}
}
