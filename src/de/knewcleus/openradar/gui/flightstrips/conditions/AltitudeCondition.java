package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class AltitudeCondition extends AbstractIntegerRangeCondition {

	public AltitudeCondition(Integer minAltitude, Integer maxAltitude, boolean isInRange) {
		super(minAltitude, maxAltitude, isInRange);
		digits = 5;
	}
	
	public AltitudeCondition(Element element) {
		super(element);
		digits = 5;
	}

	@Override
	protected String getMinAttribute() {
		return "min_altitude";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_altitude";
	}

	@Override
	protected Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData) {
		return (int) Math.round(flightstrip.getContact().getAltitude());
	}

	@Override
	public String getPrefixText() {
		return "contact's altitude is ";
	}
	
	@Override
	public String getUnit() {
		return "ft";
	}
	
}
