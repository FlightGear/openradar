package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class VFRRule extends AbstractRule {

	private final boolean isVFR;
	
	public VFRRule(boolean isVFR) {
		this.isVFR = isVFR;
	}
	
	public VFRRule(Element element, LogicManager logic) {
		this.isVFR = Boolean.valueOf(element.getAttributeValue("isvfr"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		return (flightplan != null) && (flightplan.getType().equals(FlightPlanData.FlightType.VFR.toString()) == isVFR);
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isVFR ? "" : "not") + " VFR.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isvfr", String.valueOf(isVFR));
	}

}
