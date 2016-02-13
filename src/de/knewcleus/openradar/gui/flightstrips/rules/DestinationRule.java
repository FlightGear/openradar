package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DestinationRule extends AbstractStringRule {

	public DestinationRule(String otherAirport, Boolean isOtherAirport) {
		super(otherAirport, isOtherAirport);
	}
	
	public DestinationRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getStringAttribute() {
		return "other_airport";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_other_airport";
	}

	@Override
	protected String getStringValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.getDestinationAirport();
	}

	@Override
	protected String getTextline() {
		return "contact's destination airport is " + super.getTextline();
	}
	
}
