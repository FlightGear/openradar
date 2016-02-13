package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.IDomElement;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

/* This class is the base class for each rule
 * 
 */
public abstract class AbstractRule implements IDomElement {

	abstract public Boolean isAppropriate(FlightStrip flightstrip);

	abstract public ArrayList<String> getText();

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
	
}
