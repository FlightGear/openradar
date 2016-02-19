package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class VFRCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public VFRCondition(boolean isVFR) {
		super(isVFR);
	}
	
	public VFRCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.getType().equals(FlightPlanData.FlightType.VFR.toString());
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isvfr";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "VFR.";
	}
	
}
