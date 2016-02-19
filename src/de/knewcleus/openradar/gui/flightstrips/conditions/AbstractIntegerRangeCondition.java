package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public abstract class AbstractIntegerRangeCondition extends AbstractRangeCondition {

	protected int minValue;
	protected int maxValue;
	protected int digits = 1;
	
	// --- constructors ---
	
	public AbstractIntegerRangeCondition(int minValue, int maxValue, boolean isInRange) {
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
	
	public AbstractIntegerRangeCondition(Element element) {
		super(element);
		int minValue = (element == null) ? 0 : Integer.valueOf(element.getAttributeValue(getMinAttribute()));
		int maxValue = (element == null) ? 0 : Integer.valueOf(element.getAttributeValue(getMaxAttribute()));
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
		Integer value = extractIntegerValue(flightstrip, airportData);
		if (value == null) return null;
		return (minValue <= value) && (value <= maxValue); 
	}

	protected abstract Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData);

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getMinAttribute(), String.valueOf(minValue));
		element.setAttribute(getMaxAttribute(), String.valueOf(maxValue));
	}

	// --- IRuleTextProvider ---
	
	protected String formatValue(int value) {
		return String.format("%d", value);
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
		case 3: this.minValue = Integer.parseInt(value);
		        break;
		case 5: this.maxValue = Integer.parseInt(value);
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
