package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AtcNoneRule extends AbstractBooleanRule {

	public AtcNoneRule(boolean isAtcNone) {
		super(isAtcNone);
	}
	
	public AtcNoneRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isatcnone";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.isOwnedbyNobody();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "uncontrolled.";
	}
	
}
