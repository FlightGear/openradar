package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class NeglectCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public NeglectCondition(boolean isNeglect) {
		super(isNeglect);
	}
	
	public NeglectCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().isNeglect();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isneglect";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "neglected.";
	}
	
}
