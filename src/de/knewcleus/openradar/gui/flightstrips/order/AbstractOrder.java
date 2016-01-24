package de.knewcleus.openradar.gui.flightstrips.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.DomAttributes;
import de.knewcleus.openradar.gui.flightstrips.FlightStrip;
import de.knewcleus.openradar.gui.flightstrips.FlightStripsPanel.FlightStripRow;

public abstract class AbstractOrder<T extends Comparable<T>> implements Comparator<FlightStripRow>, DomAttributes {

	protected boolean ascending = true;
	
	public AbstractOrder() {
	}

	public AbstractOrder(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	protected abstract T getCompareValue(FlightStrip flightstrip);
	
	// --- AbstractOrder ---

	public void sort(ArrayList<FlightStripRow> rows) {
		Collections.sort(rows, this);
	}

	// --- Comparator ---
	
	@Override
	public int compare(FlightStripRow fsr1, FlightStripRow fsr2) {
		int result = getCompareValue (fsr1.getFlightStrip()).compareTo(getCompareValue (fsr2.getFlightStrip()));
		return ascending ? result : -result;
	}

	// --- DomAttributes ---
	
	@Override
	public void putAttributes(Element element) {
		element.setAttribute("id", getClass().getSimpleName());
		element.setAttribute("ascending", String.valueOf(ascending));
	}

}
