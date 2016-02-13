package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class NeglectRule extends AbstractBooleanRule {

	public NeglectRule(boolean isNeglect) {
		super(isNeglect);
	}
	
	public NeglectRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isneglect";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		return flightstrip.getContact().isNeglect();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "neglected.";
	}
	
}
