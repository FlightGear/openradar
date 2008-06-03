package de.knewcleus.openradar.vessels;

import de.knewcleus.openradar.ui.core.WorkObject;

public abstract class Vessel extends WorkObject {
	protected String callsign;
	
	public Vessel(String callsign) {
		this.callsign=callsign;
	}
	
	public String getCallsign() {
		return callsign;
	}
}
