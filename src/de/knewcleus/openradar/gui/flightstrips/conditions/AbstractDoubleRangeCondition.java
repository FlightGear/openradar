package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public abstract class AbstractDoubleRangeCondition extends AbstractRangeCondition {

	private double minValue;
	private double maxValue;
	protected int digits = 1;
	protected int precision = 0;
	
	// --- constructors ---
	
	public AbstractDoubleRangeCondition(double minValue, double maxValue, boolean isInRange) {
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
	
	public AbstractDoubleRangeCondition(Element element) {
		super(element);
		double minValue = (element == null) ? 0 : Double.valueOf(element.getAttributeValue(getMinAttribute()));
		double maxValue = (element == null) ? 0 : Double.valueOf(element.getAttributeValue(getMaxAttribute()));
		if (minValue <= maxValue) {
			this.minValue = minValue;
			this.maxValue = maxValue;
		}
		else {
			this.minValue = maxValue;
			this.maxValue = minValue;
		}
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		Double value = extractDoubleValue(flightstrip, airportData);
		if (value == null) return null;
		return (minValue <= value) && (value <= maxValue); 
	}

	protected abstract Double extractDoubleValue(FlightStrip flightstrip, AirportData airportData);

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getMinAttribute(), String.valueOf(minValue));
		element.setAttribute(getMaxAttribute(), String.valueOf(maxValue));
	}

	// --- IRuleTextProvider ---
	
	protected String formatValue(double value) {
		return String.format("%.0f", value);
	}
	
	@Override
	public String getFormattedMinValue() {
		return formatValue(minValue);
	}

	@Override
	public String getFormattedMaxValue() {
		return formatValue(maxValue);
	}

	// --- IEditProvider ---
	
	@Override
	public void setStringValue(int index, String value) {
		switch (index) {
		case 3: this.minValue = Double.parseDouble(value);
		        break;
		case 5: this.maxValue = Double.parseDouble(value);
				break;
		}
		super.setStringValue(index, value);
	}

	@Override
	public String getRegExp(int index) {
		switch (index) {
		case 3:
		case 5: String result = "[0-9]{0," + digits + "}";
				if (precision > 0) result += "|" + result + "[.][0-9]{0," + precision + "}";  
				return result;
		}
		return super.getStringValue(index);
	}

	@Override
	public int getMaxLength(int index) {
		switch (index) {
		case 3:
		case 5: return digits + (precision > 0 ? 1 : 0) + precision;
		}
		return super.getMaxLength(index);
	}
	
}
