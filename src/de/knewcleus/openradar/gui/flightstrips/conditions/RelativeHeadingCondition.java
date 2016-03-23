package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class RelativeHeadingCondition extends HeadingCondition {

	// --- constructors ---
	
	public RelativeHeadingCondition(Integer minHeading, Integer maxHeading, boolean isInRange) {
		super(minHeading, maxHeading, isInRange);
	}
	
	public RelativeHeadingCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData) {
		int result = (360 - ((int) flightstrip.getContact().getRadarContactDirectionD()) + ((int) Math.round(flightstrip.getContact().getHeadingD()))) % 360;
		if (result >= 180) result = 360 - result;
		return result;
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's heading relative from the airport is ";
	}
	
}
