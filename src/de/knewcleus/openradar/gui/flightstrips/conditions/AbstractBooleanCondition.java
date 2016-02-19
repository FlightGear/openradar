package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public abstract class AbstractBooleanCondition extends AbstractCondition {

	protected boolean booleanValue;
	
	// --- constructors ---
	
	public AbstractBooleanCondition(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
	public AbstractBooleanCondition(Element element) {
		this.booleanValue = (element == null) ? true : Boolean.valueOf(element.getAttributeValue(getBooleanAttribute()));
	}
	
	// --- compare ---
	
	@Override
	public Boolean isAppropriate(FlightStrip flightstrip, AirportData airportData) {
		Boolean value = extractBooleanValue(flightstrip, airportData);
		if (value == null) return value;
		return value == booleanValue;
	}

	protected abstract Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData);
	
	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getBooleanAttribute(), String.valueOf(booleanValue));
	}

	protected String getBooleanAttribute() {
		return "is";
	}
	
	// --- IRuleTextProvider ---
	
	@Override
	public String getSimpleText() {
		return getStringValue(0) + getTextline() + getStringValue(getMaxIndex());
	}
	
	protected String getPrefixText() {
		return "contact is ";
	}
	
	protected String getTextline() {
		return booleanValue ? "" : "not ";
	}
	
	public String getSuffixText() {
		return ".";
	}
	// --- IEditProvider ---
	
	@Override
	public int getMaxIndex() {
		return super.getMaxIndex() + 3;
	}

	@Override
	public Type getType(int index) {
		switch (index) {
		case 1: return Type.LIST;
		}
		return super.getType(index);
	}

	public String getStringValue(int index) {
		switch (index) {
		case 0: return getPrefixText(); 
		case 2: return getSuffixText(); 
		}
		return super.getStringValue(index);
	}
	
	@Override
	public String[] getStringList(int index) {
		switch (index) {
		case 1: String[] result = { "", "not" }; 
				return result;
		}
		return super.getStringList(index);
	}

	@Override
	public int getIndexedValue(int index) {
		switch (index) {
		case 1: return booleanValue ? 0 : 1;
		}
		return super.getIndexedValue(index);
	}

	@Override
	public void setIndexedValue(int index, int value) {
		switch (index) {
		case 1: booleanValue = (value <= 0);
		}
		super.setIndexedValue(index, value);
	}
	
	public String getToolTipText(int index) {
		switch (index) {
		case 1: return "select option";
		}
		return super.getToolTipText(index);
	}
	
}
