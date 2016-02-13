package de.knewcleus.openradar.gui.flightstrips.rules;
import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;


public abstract class AbstractDoubleRule extends AbstractBooleanRule {

	private final double doubleValue;

	public AbstractDoubleRule(double doubleValue, boolean booleanValue) {
		super(booleanValue);
		this.doubleValue = doubleValue;
	}
	
	public AbstractDoubleRule(Element element, LogicManager logic) {
		super(element, logic);
		this.doubleValue = Double.valueOf(element.getAttributeValue(getDoubleAttribute()));
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		Double value = getDoubleValue(flightstrip);
		if (value == null) return null;
		return value < doubleValue;
	}

	@Override
	protected String getTextline() {
		return (booleanValue ? "below " : "above ") + String.format("%.0f", doubleValue);
	}
		
	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getDoubleAttribute(), String.valueOf(doubleValue));
	}

	protected abstract String getDoubleAttribute();
	protected abstract Double getDoubleValue(FlightStrip flightstrip);

}
