package de.knewcleus.openradar.gui.flightstrips.rules;

import java.util.ArrayList;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class HeadingRule extends AbstractRule {

	private final double minHeading;
	private final double maxHeading;
	private final boolean isInside;
	
	public HeadingRule(double minHeading, double maxHeading) {
		isInside = minHeading <= maxHeading; 
		if (isInside) {
			this.minHeading = minHeading;
			this.maxHeading = maxHeading;
		}
		else {
			this.minHeading = maxHeading;
			this.maxHeading = minHeading;
		}
	}
	
	public HeadingRule(Element element, LogicManager logic) {
		this(Double.valueOf(element.getAttributeValue("minheading")), 
			 Double.valueOf(element.getAttributeValue("maxheading")));
	}
	
	@Override
	public boolean isAppropriate(FlightStrip flightstrip) {
		double heading = flightstrip.getContact().getHeadingD();
		return ((minHeading <= heading) && (heading <= maxHeading)) == isInside; 
	}

	@Override
	public ArrayList<String> getRuleText() {
		ArrayList<String> result = new ArrayList<String>();
		result.add("contact's heading is between " + (isInside ? minHeading : maxHeading) + "� and " + (isInside ? maxHeading : minHeading) + "� .");
		return result;
	}

	// --- IDomElement ---
	
	@Override
	public void putAttributes(Element element) {
		super.putAttributes(element);
		element.setAttribute("minheading", String.valueOf(minHeading));
		element.setAttribute("maxheading", String.valueOf(maxHeading));
	}

}
