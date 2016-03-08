package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public abstract class AbstractIntegerRangeCondition extends AbstractRangeCondition {

	protected Integer minValue; // null = no min value
	protected Integer maxValue; // null = no max value
	protected int digits = 1;
	
	// --- constructors ---
	
	public AbstractIntegerRangeCondition(Integer minValue, Integer maxValue, boolean isInRange) {
		super(isInRange);
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	public AbstractIntegerRangeCondition(Element element) {
		super(element);
		this.minValue = getIntegerFromAttribute(element, getMinAttribute());
		this.maxValue = getIntegerFromAttribute(element, getMaxAttribute());
	}
	
	protected Integer getIntegerFromAttribute(Element element, String attribute) {
		if (element == null) return 0;
		String s = element.getAttributeValue(attribute).trim();
		return (s.length() <= 0) ? null : Integer.valueOf(s);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		Integer value = extractIntegerValue(flightstrip, airportData);
		return (value == null) ? null : (((minValue == null) ? true : (minValue <= value)) && (((maxValue == null) ? true : (value <= maxValue)))); 
	}

	protected abstract Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData);

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getMinAttribute(), (minValue == null) ? "" : String.valueOf(minValue));
		element.setAttribute(getMaxAttribute(), (maxValue == null) ? "" : String.valueOf(maxValue));
	}

	// --- IRuleTextProvider ---
	
	protected String formatValue(Integer value) {
		return (value == null) ? "" : String.format("%d", value);
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
		case 3: this.minValue = (value.length() <= 0) ? null : Integer.parseInt(value);
		        break;
		case 5: this.maxValue = (value.length() <= 0) ? null : Integer.parseInt(value);
				break;
		}
		super.setStringValue(index, value);
	}

	@Override
	public String getRegExp(int index) {
		switch (index) {
		case 3: 
		case 5: return "[0-9]{0," + digits + "}";
		}
		return super.getRegExp(index);
	}

	@Override
	public int getMaxLength(int index) {
		switch (index) {
		case 3: 
		case 5: return digits;
		}
		return super.getMaxLength(index);
	}
	
}
