package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class SquawkRule extends AbstractRule {

	private final int minSquawk;
	private final int maxSquawk;
	
	public SquawkRule(int minSquawk, int maxSquawk) {
		if (minSquawk <= maxSquawk) {
			this.minSquawk = minSquawk;
			this.maxSquawk = maxSquawk;
		}
		else {
			this.minSquawk = maxSquawk;
			this.maxSquawk = minSquawk;
		}
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		int Squawk = flightstrip.getContact().getTranspSquawkCode();
		return ((minSquawk <= Squawk) && (Squawk <= maxSquawk)); 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		if (minSquawk == maxSquawk) result.add("contact's Squawk is " + GuiRadarContact.formatSquawk(minSquawk) + ".");
		else result.add("contact's squawk code is between " + GuiRadarContact.formatSquawk(minSquawk) + " and " + GuiRadarContact.formatSquawk(maxSquawk) + ".");
		return result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("minsquawk", String.valueOf(minSquawk));
		element.setAttribute("maxsquawk", String.valueOf(maxSquawk));
	}

}
