package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
/* checks if contact is (not) ATC
 * 
 */
public class ATCRule extends AbstractRule {

	private final boolean isAtc;
	
	public ATCRule(boolean isAtc) {
		this.isAtc = isAtc;
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().isAtc() == isAtc;
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact is " + (isAtc ? "" : "not") + " ATC (e.g. OpenRadar).");
		return result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("isatc", String.valueOf(isAtc));
	}

}
