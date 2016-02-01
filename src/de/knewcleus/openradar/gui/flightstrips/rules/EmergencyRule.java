package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class EmergencyRule extends AbstractRule {

	private final boolean isEmergency;
	
	public EmergencyRule(boolean isEmergency) {
		this.isEmergency = isEmergency;
	}
	
	public EmergencyRule(Element element, LogicManager logic) {
		this.isEmergency = Boolean.valueOf(element.getAttributeValue("isemergency"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().isOnEmergency() == isEmergency;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isEmergency ? "" : "not") + " in emergency.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isemergency", String.valueOf(isEmergency));
	}

}
