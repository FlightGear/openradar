package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class ActiveRule extends AbstractRule {

	private final boolean isActive;
	
	public ActiveRule(boolean isActive) {
		this.isActive = isActive;
	}
	
	public ActiveRule(Element element, LogicManager logic) {
		this.isActive = Boolean.valueOf(element.getAttributeValue("isactive"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().isActive() == isActive;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isActive ? "" : "not") + " active.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isactive", String.valueOf(isActive));
	}

}
