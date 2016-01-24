package de.knewcleus.openradar.gui.flightstrips.actions;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.DomAttributes;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public abstract class AbstractAction implements DomAttributes {

	public abstract void executeAction(FlightStrip flightstrip);

	public abstract ArrayList<String> getActionText ();
	
	public ArrayList<AbstractAction> getActions() {
		return new ArrayList<AbstractAction>();
	}
	
	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		element.setAttribute("id", getClass().getSimpleName());
	}
	
}
