package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AltitudeRule extends AbstractDoubleRule {

	public AltitudeRule(double altitude, boolean belowAltitude) {
		super(altitude, belowAltitude);
	}
	
	public AltitudeRule(Element element, LogicManager logic) {
		super(element, logic);
	}

	@Override
	protected String getDoubleAttribute() {
		return "altitude";
	}

	@Override
	protected String getBooleanAttribute() {
		return "below";
	}

	@Override
	protected Double getDoubleValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getAltitude();
	}

	@Override
	protected String getTextline() {
		return "contact's altitude is " + super.getTextline() + " feet.";
	}
		
}
