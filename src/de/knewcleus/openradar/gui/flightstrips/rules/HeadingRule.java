package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class HeadingRule extends AbstractDoubleRangeRule {

	public HeadingRule(double minHeading, double maxHeading, boolean isInRange) {
		super(minHeading, maxHeading, isInRange);
	}
	
	public HeadingRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getMinAttribute() {
		return "min_heading";
	}

	@Override
	protected String getMaxAttribute() {
		return "max_heading";
	}

	@Override
	protected Double getDoubleValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getHeadingD();
	}

	@Override
	protected String getTextline() {
		return "contact's heading is " + super.getTextline();
	}
	
	protected String formatValue(double value) {
		return super.formatValue(value) + "°";
	}
	
}
