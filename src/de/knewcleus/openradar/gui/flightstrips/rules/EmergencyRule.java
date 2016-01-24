package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class EmergencyRule extends AbstractRule {

	private final boolean isEmergency;
	
	public EmergencyRule(boolean isEmergency) {
		this.isEmergency = isEmergency;
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().isActive() == isEmergency;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isEmergency ? "" : "not") + " in emergency.");
		return result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isemergency", String.valueOf(isEmergency));
	}

}
