package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class DirectionRule extends AbstractDoubleRangeRule {

	public DirectionRule(double minDirection, double maxDirection, boolean isInRange) {
		super(minDirection, maxDirection, isInRange);
	}
	
	public DirectionRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getMinAttribute() {
		return "min_direction";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_direction";
	}

	@Override
	protected Double getDoubleValue(FlightStrip flightstrip) {
		return (double)flightstrip.getContact().getRadarContactDirectionD();
	}

	@Override
	protected String getTextline() {
		return "contact's direction from the airport is " + super.getTextline();
	}
	
	protected String formatValue(double value) {
		return super.formatValue(value) + "°";
	}
	
}
