package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class CallsignCondition extends AbstractStringCondition {

	// --- constructors ---
	
	public CallsignCondition(String callsign, boolean isCallsign) {
		super(callsign, isCallsign);
	}
	
	public CallsignCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected String extractStringValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getCallSign();
	}

	// --- IDomElement ---
	
	@Override
	protected String getStringAttribute() {
		return "callsign";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_callsign";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's callsign is ";
	}
	
}
