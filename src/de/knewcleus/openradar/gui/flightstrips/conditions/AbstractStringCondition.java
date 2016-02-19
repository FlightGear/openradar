package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public abstract class AbstractStringCondition extends AbstractBooleanCondition {

	protected String regexp;
	
	// --- constructors ---
	
	public AbstractStringCondition(String regexp, boolean booleanValue) {
		super(booleanValue);
		this.regexp = regexp;
	}
	
	public AbstractStringCondition(Element element) {
		super(element);
		this.regexp = (element == null) ? "" : element.getAttributeValue(getStringAttribute());
	}

	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		String value = extractStringValue(flightstrip);
		if (value == null) return null;
		return value.matches(regexp);
	}
	
	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "is_like";
	}

	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute(getStringAttribute(), regexp);
	}

	protected abstract String getStringAttribute();
	protected abstract String extractStringValue(FlightStrip flightstrip);
	
	// --- IRuleTextProvider ---
	
	@Override
	protected String getTextline() {
		return super.getTextline() + "like '" + regexp + "'";
	}
	
	// --- IEditProvider ---
	
	private int mapIndex(int index) {
		return index < 2 ? index : index - 2;
	}
	
	@Override
	public int getMaxIndex() {
		return super.getMaxIndex() + 2;
	}

	@Override
	public Type getType(int index) {
		switch (index) {
		case 2: return Type.TEXT;
		case 3: return Type.STRING;
		}
		return super.getType(mapIndex(index));
	}

	public String getStringValue(int index) {
		switch (index) {
		case 2: return " like ";
		case 3: return regexp;
		}
		return super.getStringValue(mapIndex(index));
	}
	
	@Override
	public void setStringValue(int index, String value) {
		switch (index) {
		case 3: this.regexp = value;
				break;
		}
		super.setStringValue(index, value);
	}
	
	public String getToolTipText(int index) {
		switch (index) {
		case 3: return "enter a regular expression";
		}
		return super.getToolTipText(mapIndex(index));
	}
	
	public String getRegExp(int index) {
		switch (index) {
		case 3: return "[^\"]*";
		}
		return super.getRegExp(mapIndex(index));
	}
	
	public int getMaxLength(int index) {
		switch (index) {
		case 3: return -1;
		}
		return super.getMaxLength(mapIndex(index));
	}
	
}
