package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.contacts.GuiRadarContact;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class SquawkRule extends AbstractBooleanRule {

	private final int minSquawk;
	private final int maxSquawk;
	
	public SquawkRule(int minSquawk, int maxSquawk, boolean isInRange) {
		super(isInRange);
		if (minSquawk <= maxSquawk) {
			this.minSquawk = minSquawk;
			this.maxSquawk = maxSquawk;
		}
		else {
			this.minSquawk = maxSquawk;
			this.maxSquawk = minSquawk;
		}
	}
	
	public SquawkRule(Element element, LogicManager logic) {
		super(element, logic);
		int minSquawk = Integer.valueOf(element.getAttributeValue("minsquawk")); 
		int maxSquawk = Integer.valueOf(element.getAttributeValue("maxsquawk"));
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
	protected String getBooleanAttribute() {
		return "is_in_range";
	}

	@Override
	protected String getTextline() {
		return "contact's squawk code is " + super.getTextline() + ((minSquawk == maxSquawk) ? GuiRadarContact.formatSquawk(minSquawk) : "between " + GuiRadarContact.formatSquawk(minSquawk) + " and " + GuiRadarContact.formatSquawk(maxSquawk)) + ".";
	}
	
	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		int Squawk = flightstrip.getContact().getTranspSquawkCode();
		return ((minSquawk <= Squawk) && (Squawk <= maxSquawk)); 
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("minsquawk", String.valueOf(minSquawk));
		element.setAttribute("maxsquawk", String.valueOf(maxSquawk));
	}

}
