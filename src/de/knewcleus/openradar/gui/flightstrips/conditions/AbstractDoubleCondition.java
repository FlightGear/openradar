package de.knewcleus.openradar.gui.flightstrips.conditions;
import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;


public abstract class AbstractDoubleCondition extends AbstractNumberCondition {

	protected double doubleValue;
	protected int digits = 1;
	protected int precision = 0;

	// --- constructors ---
	
	public AbstractDoubleCondition(double doubleValue, boolean booleanValue) {
		super(booleanValue);
		this.doubleValue = doubleValue;
	}
	
	public AbstractDoubleCondition(Element element) {
		super(element);
		this.doubleValue = (element == null) ? 0 : Double.valueOf(element.getAttributeValue(getNumberAttribute()));
	}

	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		Double value = extractDoubleValue(flightstrip, airportData);
		if (value == null) return null;
		return value < doubleValue;
	}

	protected abstract Double extractDoubleValue(FlightStrip flightstrip, AirportData airportData);

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getNumberAttribute(), String.valueOf(doubleValue));
	}

	// --- IRuleTextProvider ---
	
	@Override
	protected String getTextline() {
		return (booleanValue ? "below " : "above ") + getFormattedValue() + getUnit();
	}

	@Override
	public String getFormattedValue() {
		return String.format("%." + precision + "f", doubleValue);
	}
	
	// --- IEditProvider ---
	
	public String getStringValue(int index) {
		switch (index) {
		case 2: return String.format("%." + precision + "f", doubleValue);
		}
		return super.getStringValue(index);
	}
	
	@Override
	public void setStringValue(int index, String value) {
		switch (index) {
		case 2: this.doubleValue = Double.parseDouble(value);
				break;
		}
		super.setStringValue(index, value);
	}
	
	@Override
	public String getRegExp(int index) {
		switch (index) {
		case 2: String result = "[0-9]{0," + digits + "}";
				if (precision > 0) result += "|" + result + "[.][0-9]{0," + precision + "}";  
				return result;
		}
		return super.getStringValue(index);
	}

	@Override
	public int getMaxLength(int index) {
		switch (index) {
		case 2: return digits + (precision > 0 ? 1 : 0) + precision;
		}
		return super.getMaxLength(index);
	}
	
	@Override
	public String[] getStringList(int index) {
		switch (index) {
		case 1: String[] result = { "below", "above" }; 
				return result;
		}
		return super.getStringList(index);
	}

}
