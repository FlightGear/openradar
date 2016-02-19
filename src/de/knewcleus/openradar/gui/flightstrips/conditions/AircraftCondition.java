package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class AircraftCondition extends AbstractStringCondition {

	// --- constructors ---
	
	public AircraftCondition(String Aircraft, boolean isAircraft) {
		super(Aircraft, isAircraft);
	}
	
	public AircraftCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected String extractStringValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getAircraftCode();
	}

	// --- IDomElement ---
	
	@Override
	protected String getStringAttribute() {
		return "aircraft";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_aircraft";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's aircraft code is ";
	}
	
}
