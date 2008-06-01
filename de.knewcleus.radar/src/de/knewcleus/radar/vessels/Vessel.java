package de.knewcleus.radar.vessels;

import de.knewcleus.radar.ui.core.WorkObject;

public abstract class Vessel extends WorkObject {
	protected String callsign;
	
	public Vessel(String callsign) {
		this.callsign=callsign;
	}
	
	public String getCallsign() {
		return callsign;
	}
}
