package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

/* This rule is true for any contact
 * 
 */
public class AnyCondition extends AbstractCondition {

	// --- constructors ---
	
	public AnyCondition(Element element) {
		super();
	}
	
	// --- compare ---
	
	@Override
	public Boolean isAppropriate(FlightStrip flightstrip, AirportData airportData) {
		return true;
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSimpleText() {
		return "any contact.";
	}
	
	// --- IEditProvider ---
	
	@Override
	public int getMaxIndex() {
		return super.getMaxIndex() + 1;
	}

	public String getStringValue(int index) {
		switch (index) {
		case 0: return getSimpleText(); 
		}
		return super.getStringValue(index);
	}
	
	public String getToolTipText(int index) {
		switch (index) {
		case 0: return "<html>WARNING: This condition is true for any contact/flightstrip.<br>Usecases:<br>Use it for the last rule to catch all remaining flightstrips or<br> use it to test rules above and ignore rules below.<br>It's useless in operator conditions (like AND, OR)</html>";
		}
		return super.getToolTipText(index);
	}
	
}
