package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DepartureHereRule extends AbstractBooleanRule {

	public DepartureHereRule(boolean isDeparting) {
		super(isDeparting);
	}
	
	public DepartureHereRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isdeparting";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.isDeparting();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "departing here.";
	}
	
}
