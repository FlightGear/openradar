package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DepartureHereRule extends AbstractRule {

	private final boolean isDeparting;
	
	public DepartureHereRule(boolean isDeparting) {
		this.isDeparting = isDeparting;
	}
	
	public DepartureHereRule(Element element, LogicManager logic) {
		this.isDeparting = Boolean.valueOf(element.getAttributeValue("isdeparting"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		return (flightplan != null) && (flightplan.isDeparting() == isDeparting);
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isDeparting ? "" : "not") + " departing here.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isdeparting", String.valueOf(isDeparting));
	}

}
