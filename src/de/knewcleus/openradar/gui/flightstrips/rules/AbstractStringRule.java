package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public abstract class AbstractStringRule extends AbstractBooleanRule {

	private final String regexp;
	
	public AbstractStringRule(String regexp, boolean booleanValue) {
		super(booleanValue);
		this.regexp = regexp;
	}
	
	public AbstractStringRule(Element element, LogicManager logic) {
		super(element, logic);
		this.regexp = element.getAttributeValue(getStringAttribute());
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		String value = getStringValue(flightstrip);
		if (value == null) return null;
		return value.matches(regexp);
	}
	
	@Override
	protected String getTextline() {
		return super.getTextline() + "like '" + regexp + "'.";
	}
	
	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getStringAttribute(), regexp);
	}

	protected abstract String getStringAttribute();
	protected abstract String getStringValue(FlightStrip flightstrip);
	
}
