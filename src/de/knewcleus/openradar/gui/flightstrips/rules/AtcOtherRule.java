package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AtcOtherRule extends AbstractStringRule {

	public AtcOtherRule(String OtherAtc, boolean isOtherAtc) {
		super(OtherAtc, isOtherAtc);
	}
	
	public AtcOtherRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getStringAttribute() {
		return "other_atc";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_other_atc";
	}

	@Override
	protected String getStringValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.getOwner();
	}

	@Override
	protected String getTextline() {
		return "contact is controlled by " + super.getTextline();
	}
	
}
