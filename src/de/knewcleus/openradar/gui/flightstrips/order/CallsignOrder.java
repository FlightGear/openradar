package de.knewcleus.openradar.gui.flightstrips.order;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class CallsignOrder extends AbstractOrder<String> {

	public CallsignOrder() {
	}

	public CallsignOrder(boolean ascending) {
		super(ascending);
	}

	public CallsignOrder(Element element) {
		super(element);
	}
	
	@Override
	protected String getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getCallSign();
	}

}
