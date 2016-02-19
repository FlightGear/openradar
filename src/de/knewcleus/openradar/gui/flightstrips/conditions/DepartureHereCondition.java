package de.knewcleus.openradar.gui.flightstrips.conditions;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightplan.FlightPlanData;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.setup.AirportData;

public class DepartureHereCondition extends AbstractBooleanCondition {

	// --- constructors ---
	
	public DepartureHereCondition(boolean isDeparting) {
		super(isDeparting);
	}
	
	public DepartureHereCondition(Element element) {
		super(element);
	}
	
	// --- compare ---
	
	@Override
	protected Boolean extractBooleanValue(FlightStrip flightstrip, AirportData airportData) {
		FlightPlanData flightplan = flightstrip.getContact().getFlightPlan();
		if (flightplan == null) return null;
		return flightplan.isDeparting();
	}

	// --- IDomElement ---
	
	@Override
	protected String getBooleanAttribute() {
		return "isdeparting";
	}

	// --- IRuleTextProvider ---
	
	@Override
	public String getSuffixText() {
		return "departing here.";
	}
	
}
