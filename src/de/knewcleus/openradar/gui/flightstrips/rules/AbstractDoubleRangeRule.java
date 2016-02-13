package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public abstract class AbstractDoubleRangeRule extends AbstractBooleanRule {

	private final double minValue;
	private final double maxValue;
	
	public AbstractDoubleRangeRule(double minValue, double maxValue, boolean isInRange) {
		super(isInRange);
		if (minValue <= maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		else {
			this.minValue = maxValue;
			this.maxValue = minValue;
		}
	}
	
	public AbstractDoubleRangeRule(Element element, LogicManager logic) {
		super(element, logic);
		double minDirection = Double.valueOf(element.getAttributeValue(getMinAttribute()));
		double maxDirection = Double.valueOf(element.getAttributeValue(getMaxAttribute()));
		if (minDirection <= maxDirection) {
			this.minValue = minDirection;
			this.maxValue = maxDirection;
		}
		else {
			this.minValue = maxDirection;
			this.maxValue = minDirection;
		}
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "is_in_range";
	}

	@Override
	protected String getTextline() {
		return super.getTextline() + "between " + formatValue(minValue) + " and " + formatValue(maxValue) + ".";
	}

	protected String formatValue(double value) {
		return String.format("%.0f", value);
	}
	
	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		Double value = getDoubleValue(flightstrip);
		if (value == null) return null;
		return (minValue <= value) && (value <= maxValue); 
	}

	protected abstract String getMinAttribute();
	protected abstract String getMaxAttribute();
	protected abstract Double getDoubleValue(FlightStrip flightstrip);

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getMinAttribute(), String.valueOf(minValue));
		element.setAttribute(getMaxAttribute(), String.valueOf(maxValue));
	}

}
