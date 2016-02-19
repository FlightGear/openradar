package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class AtcSelfCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public AtcSelfCondition(boolean isAtcSelf) {
		super(isAtcSelf);
	}
	
	public AtcSelfCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.isOwnedByMe();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isatcself";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "controlled by me.";
	}
	
}
