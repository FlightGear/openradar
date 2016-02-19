package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class NewCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public NewCondition(boolean isNew) {
		super(isNew);
	}
	
	public NewCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().isNew();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isnew";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "new.";
	}
	
}
