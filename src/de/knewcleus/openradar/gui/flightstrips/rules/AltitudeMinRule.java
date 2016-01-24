package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class AltitudeMinRule extends AbstractRule {

	private final double Altitude;
	
	public AltitudeMinRule(double Altitude) {
		this.Altitude = Altitude;
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return Altitude <= flightstrip.getContact().getAltitude(); 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's altitude is at or above " + Altitude + " feet.");
		return result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("altitude", String.valueOf(Altitude));
	}

}
