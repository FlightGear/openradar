package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class AtcLocalCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public AtcLocalCondition(boolean isAtcLocal) {
		super(isAtcLocal);
	}
	
	public AtcLocalCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().isAtc() && flightstrip.getContact().getCallSign().startsWith(airportData.getAirportCode());
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "is_atc_local";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "a local ATC.";
	}
	
}
