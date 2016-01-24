package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AircraftRule extends AbstractRule {

	private final String Aircraft; 
	private final boolean isAircraft;
	
	public AircraftRule(String Aircraft, boolean isAircraft) {
		this.Aircraft = Aircraft;
		this.isAircraft = isAircraft;
	}
	
	public AircraftRule(Element element, LogicManager logic) {
		this.Aircraft = element.getAttributeValue("aircraft");
		this.isAircraft = Boolean.valueOf(element.getAttributeValue("isaircraft"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().getAircraftCode().matches(Aircraft) == isAircraft;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's aircraft code is" + (isAircraft ? "not " : "") + "like '" + Aircraft + "'.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("aircraft", Aircraft);
		element.setAttribute("isaircraft", String.valueOf(isAircraft));
	}

}
