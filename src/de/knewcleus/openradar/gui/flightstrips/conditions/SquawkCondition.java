package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class SquawkCondition extends AbstractIntegerRangeCondition {

	// --- constructors ---
	
	public SquawkCondition(Integer minSquawk, Integer maxSquawk, boolean isInRange) {
		super(minSquawk, maxSquawk, isInRange);
		digits = 4;
	}
	
	public SquawkCondition(Element element) {
		super(element);
		digits = 4;
	}
	
	// --- compare ---
	
	@Override
	protected Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().getTranspSquawkCode();
	}

	// --- IDomElement ---
	
	@Override
	protected String getMinAttribute() {
		return "min_squawk";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_squawk";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's squawk code is ";
	}
	
	@Override
	protected String formatValue(Integer value) {
		return (value == null) ? "" : String.format("%4d", value);
	}
	
	// --- IEditProvider ---
	
	@Override
	public String getRegExp(int index) {
		switch (index) {
		case 3: 
		case 5: return "[0-7]{0,4}";
		}
		return super.getRegExp(index);
	}

}
