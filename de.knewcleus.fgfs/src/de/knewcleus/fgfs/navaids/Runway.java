package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;

public class Runway {
	protected final Position center;
	protected final String designation;
	protected final double trueHeading;
	protected final double length;
	
	public Runway(Position center, String designation, double trueHeading, double length) {
		this.center=center;
		this.designation=designation;
		this.trueHeading=trueHeading;
		this.length=length;
	}
	
	public Position getCenter() {
		return center;
	}
	
	public String getDesignation() {
		return designation;
	}
	
	public double getTrueHeading() {
		return trueHeading;
	}
	
	public double getLength() {
		return length;
	}
}
