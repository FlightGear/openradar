package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AtcSelfRule extends AbstractBooleanRule {

	public AtcSelfRule(boolean isAtcSelf) {
		super(isAtcSelf);
	}
	
	public AtcSelfRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isatcself";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.isOwnedByMe();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "controlled by me.";
	}
	
}
