package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DistanceMaxRule extends AbstractRule {

	private final double distance;
	
	public DistanceMaxRule(double distance) {
		this.distance = distance;
	}
	
	public DistanceMaxRule(Element element, LogicManager logic) {
		this.distance = Double.valueOf(element.getAttributeValue("distance"));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		return flightstrip.getContact().getRadarContactDistanceD() < distance; 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's distance from the airport is below " + distance + " nm.");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("distance", String.valueOf(distance));
	}

}
