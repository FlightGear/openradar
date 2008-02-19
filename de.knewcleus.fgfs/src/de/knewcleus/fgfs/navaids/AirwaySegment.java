package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;

public class AirwaySegment {
	protected final String startPointName,endPointName;
	protected final Position startPointPos, endPointPos;
	
	public AirwaySegment(String startPointName, Position startPointPos, String endPointName, Position endPointPos) {
		this.startPointName=startPointName;
		this.startPointPos=startPointPos;
		this.endPointName=endPointName;
		this.endPointPos=endPointPos;
	}
	
	public String getStartPointName() {
		return startPointName;
	}
	
	public Position getStartPointPos() {
		return startPointPos;
	}
	
	public String getEndPointName() {
		return endPointName;
	}
	
	public Position getEndPointPos() {
		return endPointPos;
	}
}
