package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;

public class AbstractNavaid extends AbstractNamedFix {

	protected final String name;
	protected final double frequency;
	protected final double range;

	public AbstractNavaid(String id, Position position, String name, double frequency, double range) {
		super(id, position);
		this.name=name;
		this.frequency=frequency;
		this.range=range;
	}

	public String getName() {
		return name;
	}

	public double getFrequency() {
		return frequency;
	}

	public double getRange() {
		return range;
	}

}