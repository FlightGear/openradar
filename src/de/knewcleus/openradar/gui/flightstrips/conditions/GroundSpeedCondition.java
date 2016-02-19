package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class GroundSpeedCondition extends AbstractIntegerRangeCondition {

	public GroundSpeedCondition(int minGroundSpeed, int maxGroundSpeed, boolean belowGroundSpeed) {
		super(minGroundSpeed, maxGroundSpeed, belowGroundSpeed);
		digits = 3;
	}
	
	public GroundSpeedCondition(Element element) {
		super(element);
		digits = 3;
	}

	@Override
	protected String getMinAttribute() {
		return "min_ground_speed";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_ground_speed";
	}

	@Override
	protected Integer extractIntegerValue(FlightStrip flightstrip, AirportData airportData) {
		return (int) Math.round(flightstrip.getContact().getGroundSpeedD());
	}

	@Override
	public String getPrefixText() {
		return "contact's ground speed is ";
	}
	
	@Override
	public String getUnit() {
		return "kn";
	}
	
}
