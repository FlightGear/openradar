package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.IDomElement;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public abstract class AbstractAction implements IDomElement {

	public abstract void executeAction(FlightStrip flightstrip);

	public abstract ArrayList<String> getActionText ();
	
	public ArrayList<AbstractAction> getActions() {
		return new ArrayList<AbstractAction>();
	}
	
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
