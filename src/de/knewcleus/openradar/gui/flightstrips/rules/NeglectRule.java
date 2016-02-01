package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class NeglectRule extends AbstractRule {

	private final boolean isNeglect;
	
	public NeglectRule(boolean isNeglect) {
		this.isNeglect = isNeglect;
	}
	
	public NeglectRule(Element element, LogicManager logic) {
		this.isNeglect = Boolean.valueOf(element.getAttributeValue("isneglect"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().isNeglect() == isNeglect;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isNeglect ? "" : "not") + " neglect.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isneglect", String.valueOf(isNeglect));
	}

}
