package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class AtcNoneCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public AtcNoneCondition(boolean isAtcNone) {
		super(isAtcNone);
	}
	
	public AtcNoneCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.isOwnedbyNobody();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isatcnone";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "uncontrolled.";
	}
	
}
