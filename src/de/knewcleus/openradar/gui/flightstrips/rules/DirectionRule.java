package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DirectionRule extends AbstractRule {

	private final double minDirection;
	private final double maxDirection;
	private final boolean isInside;
	
	public DirectionRule(double minDirection, double maxDirection) {
		isInside = minDirection <= maxDirection; 
		if (isInside) {
			this.minDirection = minDirection;
			this.maxDirection = maxDirection;
		}
		else {
			this.minDirection = maxDirection;
			this.maxDirection = minDirection;
		}
	}
	
	public DirectionRule(Element element, LogicManager logic) {
		this(Double.valueOf(element.getAttributeValue("mindirection")), 
			 Double.valueOf(element.getAttributeValue("maxdirection")));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		double Direction = flightstrip.getContact().getRadarContactDirectionD();
		return ((minDirection <= Direction) && (Direction <= maxDirection)) == isInside; 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's direction from the airport is between " + (isInside ? minDirection : maxDirection) + "° and " + (isInside ? maxDirection : minDirection) + "° .");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("mindirection", String.valueOf(minDirection));
		element.setAttribute("maxdirection", String.valueOf(maxDirection));
	}

}
