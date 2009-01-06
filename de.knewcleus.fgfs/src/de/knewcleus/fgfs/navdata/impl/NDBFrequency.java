package de.knewcleus.fgfs.navdata.impl;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.navdata.model.IFrequency;

public class NDBFrequency implements IFrequency {
	protected final float freqKHz;
	
	public NDBFrequency(float freqKHz) {
		this.freqKHz = freqKHz;
	}
	
	@Override
	public String toString() {
		return String.format("%05.1f", freqKHz);
	}

	@Override
	public float getValue() {
		return freqKHz*Units.KHZ;
	}

}
