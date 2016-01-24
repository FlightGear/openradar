package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.DomAttributes;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

/* This class is the base class for each rule
 * 
 */
public abstract class AbstractRule implements DomAttributes {

	abstract public boolean isAppropriate(FlightStrip flightstrip);

	abstract public ArrayList<String> getRuleText();

	public ArrayList<AbstractRule> getRules() {
		// override to provide a list of rules
		return new ArrayList<AbstractRule>();
	}
	
	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		element.setAttribute("id", getClass().getSimpleName());
	}

}
