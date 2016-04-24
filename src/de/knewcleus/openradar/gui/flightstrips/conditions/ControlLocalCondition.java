package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class ControlLocalCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public ControlLocalCondition(boolean isAtcLocal) {
		super(isAtcLocal);
	}
	
	public ControlLocalCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.getOwner().startsWith(airportData.getAirportCode());
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "is_owner_local";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "controlled by a local ATC.";
	}
	
}
