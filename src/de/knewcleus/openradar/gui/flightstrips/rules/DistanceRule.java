package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DistanceRule extends AbstractDoubleRule {

	public DistanceRule(double distance, boolean belowDistance) {
		super(distance, belowDistance);
	}
	
	public DistanceRule(Element element, LogicManager logic) {
		super(element, logic);
	}

	@Override
	protected String getDoubleAttribute() {
		return "distance";
	}

	@Override
	protected String getBooleanAttribute() {
		return "below";
	}

	@Override
	protected Double getDoubleValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getRadarContactDistanceD();
	}

	@Override
	protected String getTextline() {
		return "contact's distance from the airport is " + super.getTextline() + " nm.";
	}
		
}
