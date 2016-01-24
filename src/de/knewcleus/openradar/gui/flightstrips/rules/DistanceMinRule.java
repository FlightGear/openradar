package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DistanceMinRule extends AbstractRule {

	private final double distance;
	
	public DistanceMinRule(double distance) {
		this.distance = distance;
	}
	
	public DistanceMinRule(Element element, LogicManager logic) {
		this.distance = Double.valueOf(element.getAttributeValue("distance"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return distance <= flightstrip.getContact().getRadarContactDistanceD(); 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's distance from the airport is at or above " + distance + " nm.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("distance", String.valueOf(distance));
	}

}
