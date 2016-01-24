package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AltitudeMaxRule extends AbstractRule {

	private final double altitude;
	
	public AltitudeMaxRule(double altitude) {
		this.altitude = altitude;
	}
	
	public AltitudeMaxRule(Element element, LogicManager logic) {
		this.altitude = Double.valueOf(element.getAttributeValue("altitude"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().getAltitude() < altitude; 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's altitude is below " + altitude + " feet.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("altitude", String.valueOf(altitude));
	}

}
