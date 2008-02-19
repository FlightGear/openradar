package de.knewcleus.fgfs.navaids;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Airway {
	protected final String designator;
	protected Set<AirwaySegment> segments=new HashSet<AirwaySegment>();
	protected Map<String, AirwaySegment> segmentsByStartPoint=new HashMap<String, AirwaySegment>();
	protected Map<String, AirwaySegment> segmentsByEndPoint=new HashMap<String, AirwaySegment>();

	public Airway(String designator) {
		this.designator=designator;
	}
	
	public void addSegment(AirwaySegment airwaySegment) {
		segments.add(airwaySegment);
		segmentsByStartPoint.put(airwaySegment.getStartPointName(),airwaySegment);
		segmentsByEndPoint.put(airwaySegment.getEndPointName(),airwaySegment);
	}
	
	public Set<AirwaySegment> getSegments() {
		return Collections.unmodifiableSet(segments);
	}
	
	public String getDesignator() {
		return designator;
	}
}
