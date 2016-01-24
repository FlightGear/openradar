package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AltitudeMinRule extends AbstractRule {

	private final double altitude;
	
	public AltitudeMinRule(double altitude) {
		this.altitude = altitude;
	}
	
	public AltitudeMinRule(Element element, LogicManager logic) {
		this.altitude = Double.valueOf(element.getAttributeValue("altitude"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return altitude <= flightstrip.getContact().getAltitude(); 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's altitude is at or above " + altitude + " feet.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("altitude", String.valueOf(altitude));
	}

}
