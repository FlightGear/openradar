package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public abstract class AbstractBooleanRule extends AbstractRule {

	protected final boolean booleanValue;
	
	public AbstractBooleanRule(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	public AbstractBooleanRule(Element element, LogicManager logic) {
		this.booleanValue = Boolean.valueOf(element.getAttributeValue(getBooleanAttribute()));
	}
	
	@Override
	public Boolean isAppropriate(FlightStrip flightstrip) {
		Boolean value = getBooleanValue(flightstrip);
		if (value == null) return value;
		return value == booleanValue;
	}

	@Override
	public ArrayList<String> getText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(getTextline());
		return result;
	}

	protected String getTextline() {
		return booleanValue ? "" : "not ";
	}
	
	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getBooleanAttribute(), String.valueOf(booleanValue));
	}

	protected abstract String getBooleanAttribute();
	protected abstract Boolean getBooleanValue(FlightStrip flightstrip);
	
}
