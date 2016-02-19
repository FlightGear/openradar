package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class DistanceCondition extends AbstractDoubleRangeCondition {

	// --- constructors ---
	
	public DistanceCondition(double minDistance, double maxDistance, boolean belowDistance) {
		super(minDistance, maxDistance, belowDistance);
		digits = 3;
	}
	
	public DistanceCondition(Element element) {
		super(element);
		digits = 3;
	}

	// --- compare ---
	
	@Override
	protected Double extractDoubleValue(FlightStrip flightstrip, AirportData airportData) {
		return flightstrip.getContact().getRadarContactDistanceD();
	}
	
	// --- IDomElement ---
	
	@Override
	protected String getMinAttribute() {
		return "min_distance";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_distance";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getPrefixText() {
		return "contact's distance from the airport is ";
	}
	
	@Override
	public String getUnit() {
		return "nm";
	}
	
}
