package de.knewcleus.openradar.gui.flightstrips.order;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class AltitudeOrder extends AbstractOrder<Double> {

	public AltitudeOrder() {
	}

	public AltitudeOrder(boolean ascending) {
		super(ascending);
	}

	public AltitudeOrder(Element element) {
		super(element);
	}
	
	@Override
	public String getDisplayName() {
		return "altitude";
	}

	@Override
	protected Double getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getAltitude();
	}

}
