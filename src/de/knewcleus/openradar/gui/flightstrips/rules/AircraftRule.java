package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class AircraftRule extends AbstractStringRule {

	public AircraftRule(String Aircraft, boolean isAircraft) {
		super(Aircraft, isAircraft);
	}
	
	public AircraftRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getStringAttribute() {
		return "aircraft";
	}

	@Override
	protected String getBooleanAttribute() {
		return "is_aircraft";
	}

	@Override
	protected String getStringValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getAircraftCode();
	}

	@Override
	protected String getTextline() {
		return "contact's aircraft code is " + super.getTextline();
	}
	
}
