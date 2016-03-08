package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public abstract class AbstractDoubleRangeCondition extends AbstractRangeCondition {

	private Double minValue;
	private Double maxValue;
	protected int digits = 1;
	protected int precision = 0;
	
	// --- constructors ---
	
	public AbstractDoubleRangeCondition(Double minValue, Double maxValue, boolean isInRange) {
		super(isInRange);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public AbstractDoubleRangeCondition(Element element) {
		super(element);
		this.minValue = getDoubleFromAttribute(element, getMinAttribute());
		this.maxValue = getDoubleFromAttribute(element, getMaxAttribute());
	}
	
	protected Double getDoubleFromAttribute(Element element, String attribute) {
		if (element == null) return 0.0;
		String s = element.getAttributeValue(attribute).trim();
		return (s.length() <= 0) ? null : Double.valueOf(s);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		Double value = extractDoubleValue(flightstrip, airportData);
		if (value == null) return null;
		return (value == null) ? null : (((minValue == null) ? true : (minValue <= value)) && (((maxValue == null) ? true : (value <= maxValue)))); 
	}

	protected abstract Double extractDoubleValue(FlightStrip flightstrip, AirportData airportData);

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getMinAttribute(), (minValue == null) ? "" : String.valueOf(minValue));
		element.setAttribute(getMaxAttribute(), (maxValue == null) ? "" : String.valueOf(maxValue));
	}

	// --- IRuleTextProvider ---
	
	protected String formatValue(Double value) {
		return (value == null) ? "" : String.format("%.0f", value);
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
		case 3: this.minValue = (value.length() <= 0) ? null : Double.parseDouble(value);
		        break;
		case 5: this.maxValue = (value.length() <= 0) ? null : Double.parseDouble(value);
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
