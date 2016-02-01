package de.knewcleus.openradar.gui.flightstrips.order;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class ColumnOrder extends AbstractOrder<Integer> {

	public ColumnOrder() {
	}

	public ColumnOrder(boolean ascending) {
		super(ascending);
	}

	public ColumnOrder(Element element) {
		super(element);
	}
	
	@Override
	protected Integer getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getColumn();
	}

}
