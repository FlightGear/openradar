package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class IFRRule extends AbstractBooleanRule {

	public IFRRule(boolean isIFR) {
		super(isIFR);
	}
	
	public IFRRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isifr";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.getType().equals(FlightPlanData.FlightType.IFR.toString());
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "IFR.";
	}
	
}
