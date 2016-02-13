package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DestinationHereRule extends AbstractBooleanRule {

	public DestinationHereRule(boolean isLanding) {
		super(isLanding);
	}
	
	public DestinationHereRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "islanding";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.contactWillLandHere();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "landing here.";
	}
	
}
