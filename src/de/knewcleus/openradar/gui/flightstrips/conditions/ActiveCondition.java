package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class ActiveCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public ActiveCondition(boolean isActive) {
		super(isActive);
	}
	
	public ActiveCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().isActive();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isactive";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "active.";
	}
	
}
