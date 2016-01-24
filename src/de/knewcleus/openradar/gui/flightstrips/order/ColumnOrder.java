package de.knewcleus.openradar.gui.flightstrips.order;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class ColumnOrder extends AbstractOrder<Integer> {

	public ColumnOrder() {
	}

	public ColumnOrder(boolean ascending) {
		super(ascending);
	}

	@Override
	protected Integer getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getColumn();
	}

}
