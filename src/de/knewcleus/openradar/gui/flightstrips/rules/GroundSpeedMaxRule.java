package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class GroundSpeedMaxRule extends AbstractRule {

	private final double groundSpeed;
	
	public GroundSpeedMaxRule(double groundSpeed) {
		this.groundSpeed = groundSpeed;
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().getGroundSpeedD() < groundSpeed; 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's ground speed is below " + groundSpeed + " knots.");
		return result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("groundspeed", String.valueOf(groundSpeed));
	}

}
