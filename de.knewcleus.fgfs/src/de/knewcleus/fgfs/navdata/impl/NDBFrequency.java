package de.knewcleus.fgfs.navdata.impl;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;

public class NDBFrequency implements IFrequency {
	protected final float freqKHz;
	
	public NDBFrequency(float freqKHz) {
		this.freqKHz = freqKHz;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IFrequency)) {
			return false;
		}
		final IFrequency other = (IFrequency)obj;
		return Math.abs(other.getValue() - getValue()) < 0.25 * Units.KHz;
	}
	
	@Override
	public String toString() {
		return String.format("%05.1f", freqKHz);
	}

	@Override
	public float getValue() {
		return freqKHz*Units.KHz;
	}

}