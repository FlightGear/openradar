package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;
/* checks if contact is (not) ATC
 * 
 */
public class ATCCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public ATCCondition(boolean isAtc) {
		super(isAtc);
	}
	
	public ATCCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().isAtc();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isatc";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "ATC (e.g. OpenRadar).";
	}
	
}
