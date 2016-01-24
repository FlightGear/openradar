package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AtcSelfRule extends AbstractRule {

	private final boolean isAtcSelf;
	
	public AtcSelfRule(boolean isAtcSelf) {
		this.isAtcSelf = isAtcSelf;
	}
	
	public AtcSelfRule(Element element, LogicManager logic) {
		this.isAtcSelf = Boolean.valueOf(element.getAttributeValue("isatcself"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		return (flightplan != null) && (flightplan.isOwnedByMe() == isAtcSelf);
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isAtcSelf ? "" : "not") + " controlled by me.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isatcself", String.valueOf(isAtcSelf));
	}

}
