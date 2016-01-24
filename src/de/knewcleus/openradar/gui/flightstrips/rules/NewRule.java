package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class NewRule extends AbstractRule {

	private final boolean isNew;
	
	public NewRule(boolean isNew) {
		this.isNew = isNew;
	}
	
	public NewRule(Element element, LogicManager logic) {
		this.isNew = Boolean.valueOf(element.getAttributeValue("isnew"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().isNew() == isNew;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isNew ? "" : "not") + " new.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isnew", String.valueOf(isNew));
	}

}
