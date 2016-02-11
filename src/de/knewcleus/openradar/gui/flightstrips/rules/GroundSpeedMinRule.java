package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class GroundSpeedMinRule extends AbstractRule {

	private final double groundSpeed;
	
	public GroundSpeedMinRule(double groundSpeed) {
		this.groundSpeed = groundSpeed;
	}
	
	public GroundSpeedMinRule(Element element, LogicManager logic) {
		this.groundSpeed = Double.valueOf(element.getAttributeValue("groundspeed"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return groundSpeed <= flightstrip.getContact().getGroundSpeedD(); 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's ground speed is at or above " + groundSpeed + " knots.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("groundspeed", String.valueOf(groundSpeed));
	}

}