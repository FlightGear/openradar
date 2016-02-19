package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class DirectionCondition extends AbstractDoubleRangeCondition {

	// --- constructors ---
	
	public DirectionCondition(double minDirection, double maxDirection, boolean isInRange) {
		super(minDirection, maxDirection, isInRange);
		digits = 3;
	}
	
	public DirectionCondition(Element element) {
		super(element);
		digits = 3;
	}
	
	// --- compare ---
		
	@Override
	protected Double extractDoubleValue(FlightStrip flightstrip, AirportData airportData) {
		return (double)flightstrip.getContact().getRadarContactDirectionD();
	}

	// --- IDomElement ---
	
	@Override
	protected String getMinAttribute() {
		return "min_direction";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_direction";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's direction from the airport is ";
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
		case 5: String result = "360|3[0-5][0-9]|[0-2]{0,1}[0-9]{0,2}";
				if (precision > 0) result += "|" + result + "[.][0-9]{0," + precision + "}";  
				return result;
		}
		return super.getStringValue(index);
	}

}
