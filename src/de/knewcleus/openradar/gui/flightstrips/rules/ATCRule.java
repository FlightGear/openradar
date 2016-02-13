package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;
/* checks if contact is (not) ATC
 * 
 */
public class ATCRule extends AbstractBooleanRule {

	public ATCRule(boolean isAtc) {
		super(isAtc);
	}
	
	public ATCRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isatc";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		return flightstrip.getContact().isAtc();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "ATC (e.g. OpenRadar).";
	}
	
}
