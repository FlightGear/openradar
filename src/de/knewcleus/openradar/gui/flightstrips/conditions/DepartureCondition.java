package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class DepartureCondition extends AbstractStringCondition {

	// --- constructors ---
	
	public DepartureCondition(String otherAirport, Boolean isOtherAirport) {
		super(otherAirport, isOtherAirport);
	}
	
	public DepartureCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected String extractStringValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.getDepartureAirport();
	}

	// --- IDomElement ---
	
	@Override
	protected String getStringAttribute() {
		return "other_airport";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_other_airport";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's departure airport is ";
	}
	
}
