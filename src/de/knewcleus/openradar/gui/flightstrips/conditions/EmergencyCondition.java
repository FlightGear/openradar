package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class EmergencyCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public EmergencyCondition(boolean isEmergency) {
		super(isEmergency);
	}
	
	public EmergencyCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().isOnEmergency();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isemergency";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "in emergency.";
	}
	
}
