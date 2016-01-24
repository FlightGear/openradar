package de.knewcleus.openradar.gui.flightstrips.order;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class DistanceOrder extends AbstractOrder<Double> {

	public DistanceOrder() {
	}

	public DistanceOrder(boolean ascending) {
		super(ascending);
	}

	@Override
	protected Double getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getRadarContactDistanceD();
	}

}
