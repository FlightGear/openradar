package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public abstract class AbstractIntegerCondition extends AbstractNumberCondition {

	protected int intValue;
	protected int digits = 1;

	// --- constructors ---
	
	public AbstractIntegerCondition(int intValue, boolean booleanValue) {
		super(booleanValue);
		this.intValue = intValue;
	}
	
	public AbstractIntegerCondition(Element element) {
		super(element);
		this.intValue = (element == null) ? 0 : Integer.valueOf(element.getAttributeValue(getNumberAttribute()));
	}

	// --- AbstractBooleanCondition ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		Integer value = extractIntegerValue(flightstrip);
		if (value == null) return null;
		return value == intValue;
	}

	protected abstract Integer extractIntegerValue(FlightStrip flightstrip);

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getNumberAttribute(), String.valueOf(intValue));
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getFormattedValue() {
		return String.format("%d", intValue);
	}
	
	// --- IRegExpProvider ---
	
	public String getStringValue(int index) {
		switch (index) {
		case 2: return String.format("%d", intValue);
		}
		return super.getStringValue(index);
	}
	
	@Override
	public void setStringValue(int index, String value) {
		switch (index) {
		case 2: try {
					this.intValue = Integer.parseInt(value);
				} catch (NumberFormatException e) {
					this.intValue = 0;
				}
				break;
		}
		super.setStringValue(index, value);
	}

	@Override
	public String getRegExp(int index) {
		switch (index) {
		case 2: return "[0-9]{0," + digits + "}";
		}
		return super.getRegExp(index);
	}

	@Override
	public int getMaxLength(int index) {
		switch (index) {
		case 2: return digits;
		}
		return super.getMaxLength(index);
	}
	
}
