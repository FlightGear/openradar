package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class DestinationCondition extends AbstractStringCondition {

	// --- constructors ---
	
	public DestinationCondition(String otherAirport, Boolean isOtherAirport) {
		super(otherAirport, isOtherAirport);
	}
	
	public DestinationCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected String extractStringValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.getDestinationAirport();
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
		return "contact's destination airport is ";
	}
	
}
