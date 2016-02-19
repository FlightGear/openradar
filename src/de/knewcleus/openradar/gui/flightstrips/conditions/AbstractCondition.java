package de.knewcleus.openradar.gui.flightstrips.conditions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.IDomElement;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.config.IEditProvider;
import de.knewcleus.openradar.gui.flightstrips.config.IRuleTextProvider;
import de.knewcleus.openradar.gui.setup.AirportData;

/* This class is the base class for each condition
 * 
 */
public abstract class AbstractCondition implements IDomElement, IRuleTextProvider, IEditProvider {

	// --- compare ---
	
	abstract public Boolean isAppropriate(FlightStrip flightstrip, AirportData airportData);

	// --- IDomElement ---
	
	@Override
	public String getDomElementName() {
		return getClass().getSimpleName();
	}

	@Override
	public Element createDomElement() {
		Element element = new Element(getDomElementName());
		putAttributes(element);
		return element;
	}

	public void putAttributes(Element element) {
	}
	
	// --- IRuleTextProvider ---
	
	public abstract String getSimpleText();

	public ArrayList<String> getText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(getSimpleText());
		return result;
	}

	// --- IEditProvider ---
	
	@Override
	public int getMaxIndex() {
		return -1;
	}

	@Override
	public Type getType(int index) {
		return Type.TEXT;
	}

	@Override
	public String getStringValue(int index) {
		return "";
	}
	
	@Override
	public void setStringValue(int index, String value) {
	}
	
	@Override
	public String getRegExp(int index) {
		return "";
	}

	@Override
	public int getMaxLength(int index) {
		return -1;
	}
	
	@Override
	public String[] getStringList(int index) {
		return null;
	}

	@Override
	public int getIndexedValue(int index) {
		return 0;
	}

	@Override
	public void setIndexedValue(int index, int value) {
	}
	
	@Override
	public String getToolTipText(int index) {
		return "";
	}
	
}
