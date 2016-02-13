package de.knewcleus.openradar.gui.flightstrips.rules;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.LogicManager;

public class NewRule extends AbstractBooleanRule {

	public NewRule(boolean isNew) {
		super(isNew);
	}
	
	public NewRule(Element element, LogicManager logic) {
		super(element, logic);
	}
	
	@Override
	protected String getBooleanAttribute() {
		return "isnew";
	}

	@Override
	protected Boolean getBooleanValue(FlightStrip flightstrip) {
		return flightstrip.getContact().isNew();
	}

	@Override
	protected String getTextline() {
		return "contact is " + super.getTextline() + "new.";
	}
	
}
