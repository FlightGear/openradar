package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class AircraftRule extends AbstractRule {

	private final String Aircraft; 
	private final boolean isAircraft;
	
	public AircraftRule(String Aircraft, boolean isAircraft) {
		this.Aircraft = Aircraft;
		this.isAircraft = isAircraft;
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

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("aircraft", Aircraft);
		element.setAttribute("isaircraft", String.valueOf(isAircraft));
	}

}
