package de.knewcleus.openradar.gui.flightstrips.order;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class AltitudeOrder extends AbstractOrder<Double> {

	public AltitudeOrder() {
	}

	public AltitudeOrder(boolean ascending) {
		super(ascending);
	}

	@Override
	protected Double getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getAltitude();
	}

}
