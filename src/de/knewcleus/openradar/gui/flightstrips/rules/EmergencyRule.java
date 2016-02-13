package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class EmergencyRule extends AbstractBooleanRule {

	public EmergencyRule(boolean isEmergency) {
		super(isEmergency);
	}
	
	public EmergencyRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isemergency";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		return flightstrip.getContact().isOnEmergency();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "in emergency.";
	}
	
}
