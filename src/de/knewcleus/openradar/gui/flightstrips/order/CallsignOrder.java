package de.knewcleus.openradar.gui.flightstrips.order;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.FlightStripsPanel.FlightStripRow;

public class CallsignOrder extends AbstractOrder<String> {

	public CallsignOrder() {
	}

	public CallsignOrder(boolean ascending) {
		super(ascending);
	}

	public CallsignOrder(Element element) {
		super(element);
	}
	
	@Override
	public String getDisplayName() {
		return "callsign";
	}

	@Override
	protected String getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getCallSign();
	}

	// --- Comparator ---
	
	@Override
	public int compare(FlightStripRow fsr1, FlightStripRow fsr2) {
		int result = getCompareValue (fsr1.getFlightStrip()).compareToIgnoreCase(getCompareValue (fsr2.getFlightStrip()));
		return ascending ? result : -result;
	}

}
