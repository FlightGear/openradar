package de.knewcleus.openradar.gui.flightstrips.order;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class CallsignOrder extends AbstractOrder<String> {

	public CallsignOrder() {
	}

	public CallsignOrder(boolean ascending) {
		super(ascending);
	}

	@Override
	protected String getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getCallSign();
	}

}
