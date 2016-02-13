package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class GroundSpeedRule extends AbstractDoubleRule {

	public GroundSpeedRule(double groundSpeed, boolean belowGroundSpeed) {
		super(groundSpeed, belowGroundSpeed);
	}
	
	public GroundSpeedRule(Element element, LogicManager logic) {
		super(element, logic);
	}

	@Override
	protected String getDoubleAttribute() {
		return "groundspeed";
	}

	@Override
	protected String getBooleanAttribute() {
		return "below";
	}

	@Override
	protected Double getDoubleValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getGroundSpeedD();
	}

	@Override
	protected String getTextline() {
		return "contact's ground speed is " + super.getTextline() + " knots.";
	}
		
}
