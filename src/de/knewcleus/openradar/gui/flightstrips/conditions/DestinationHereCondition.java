package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class DestinationHereCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public DestinationHereCondition(boolean isLanding) {
		super(isLanding);
	}
	
	public DestinationHereCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.contactWillLandHere();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "islanding";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "landing here.";
	}
	
}
