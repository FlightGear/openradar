package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class AGLCondition extends AltitudeCondition {

	public AGLCondition(int minAltitude, int maxAltitude, boolean isInRange) {
		super(minAltitude, maxAltitude, isInRange);
		digits = 4;
	}
	
	public AGLCondition(Element element) {
		super(element);
		digits = 4;
	}

	@Override
	protected Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData) {
		return ((int) Math.round(flightstrip.getContact().getAltitude() - airportData.getElevationFt()));
	}

	@Override
	public String getPrefixText() {
		return "contact's altitude above ground (AGL) is ";
	}
	
}
