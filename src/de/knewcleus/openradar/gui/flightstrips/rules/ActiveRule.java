package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class ActiveRule extends AbstractBooleanRule {

	public ActiveRule(boolean isActive) {
		super(isActive);
	}
	
	public ActiveRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isactive";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		return flightstrip.getContact().isActive();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "active.";
	}
	
}
