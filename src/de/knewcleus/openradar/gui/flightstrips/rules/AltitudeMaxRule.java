package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class AltitudeMaxRule extends AbstractRule {

	private final double Altitude;
	
	public AltitudeMaxRule(double Altitude) {
		this.Altitude = Altitude;
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().getAltitude() < Altitude; 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's altitude is below " + Altitude + " feet.");
		return result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("altitude", String.valueOf(Altitude));
	}

}
