package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;

public class VOR extends AbstractNavaid {
	protected final double variation;
	
	public VOR(String id, Position position, String name, double frequency, double range, double variation) {
		super(id, position,name,frequency,range);
		this.variation=variation;
	}
	
	public double getVariation() {
		return variation;
	}
}
