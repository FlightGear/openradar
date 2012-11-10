package de.knewcleus.fgfs.navdata.impl;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;

public class VORFrequency implements IFrequency {
	protected final float freqMHz;
	
	public VORFrequency(float freqMHz) {
		this.freqMHz = freqMHz;
	}
	
	@Override
	public String toString() {
		return String.format("%05.1f", freqMHz);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IFrequency)) {
			return false;
		}
		final IFrequency other = (IFrequency)obj;
		return Math.abs(other.getValue() - getValue()) < 0.05 * Units.MHz;
	}

	@Override
	public float getValue() {
		return freqMHz * Units.MHz;
	}

}
