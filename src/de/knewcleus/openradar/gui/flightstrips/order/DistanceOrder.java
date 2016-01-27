package de.knewcleus.openradar.gui.flightstrips.order;

import org.jdom2.Element;

import de.knewcleus.openradar.gui.flightstrips.FlightStrip;

public class DistanceOrder extends AbstractOrder<Double> {

	public DistanceOrder() {
	}

	public DistanceOrder(boolean ascending) {
		super(ascending);
	}

	public DistanceOrder(Element element) {
		super(element);
	}
	
	@Override
	public String getDisplayName() {
		return "distance";
	}

	@Override
	protected Double getCompareValue(FlightStrip flightstrip) {
		return flightstrip.getContact().getRadarContactDistanceD();
	}

}
