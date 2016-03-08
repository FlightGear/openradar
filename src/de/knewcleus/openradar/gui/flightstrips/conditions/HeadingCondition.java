package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class HeadingCondition extends AbstractIntegerRangeCondition {

	// --- constructors ---
	
	public HeadingCondition(Integer minHeading, Integer maxHeading, boolean isInRange) {
		super(minHeading, maxHeading, isInRange);
		digits = 3;
	}
	
	public HeadingCondition(Element element) {
		super(element);
		digits = 3;
	}
	
	// --- compare ---
	
	@Override
	protected Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData) {
		return (int) Math.round(flightstrip.getContact().getHeadingD());
	}

	// --- IDomElement ---
	
	@Override
	protected String getMinAttribute() {
		return "min_heading";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_heading";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's heading is ";
	}
	
	@Override
	public String getUnit() {
		return "°";
	}
	
	// --- IEditProvider ---
	
	@Override
	public String getRegExp(int index) {
		switch (index) {
		case 3: 
		case 5: return "360|3[0-5][0-9]|[0-2]{0,1}[0-9]{0,2}";
		}
		return super.getRegExp(index);
	}

}
