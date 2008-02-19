package de.knewcleus.radar.sector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.knewcleus.fgfs.location.Position;

public class PolygonContour implements Iterable<Position> {
	protected final List<Position> points=new ArrayList<Position>();
	protected final boolean isHole;
	
	public PolygonContour(boolean isHole) {
		this.isHole=isHole;
	}
	
	public void addPoint(Position pos) {
		points.add(pos);
	}
	
	public List<Position> getPoints() {
		return points;
	}
	
	public Iterator<Position> iterator() {
		return points.iterator();
	}
}
