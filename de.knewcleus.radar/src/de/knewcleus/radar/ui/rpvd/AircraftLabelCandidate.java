package de.knewcleus.radar.ui.rpvd;

import de.knewcleus.radar.autolabel.LabelCandidate;
import de.knewcleus.radar.autolabel.LabeledObject;

public class AircraftLabelCandidate implements LabelCandidate {
	protected final AircraftSymbol associatedSymbol;
	protected final double dx,dy;
	protected final double cost;
	
	public AircraftLabelCandidate(AircraftSymbol associatedSymbol, double dx, double dy, double cost) {
		this.associatedSymbol=associatedSymbol;
		this.dx=dx;
		this.dy=dy;
		this.cost=cost;
	}

	@Override
	public LabeledObject getAssociatedObject() {
		return associatedSymbol;
	}

	@Override
	public double getCost() {
		return cost;
	}
	
	public double getDx() {
		return dx;
	}
	
	public double getDy() {
		return dy;
	}

	@Override
	public double getTop() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double r=AircraftSymbol.aircraftSymbolSize/2+AircraftSymbol.leadingLineLength;
		double h=associatedSymbol.getCurrentLabelHeight();
		return y+dy*r+(dy-1.0)*h/2.0;
	}

	@Override
	public double getBottom() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double r=AircraftSymbol.aircraftSymbolSize/2+AircraftSymbol.leadingLineLength;
		double h=associatedSymbol.getCurrentLabelHeight();
		return y+dy*r+(dy+1.0)*h/2.0;
	}

	@Override
	public double getLeft() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double r=AircraftSymbol.aircraftSymbolSize/2+AircraftSymbol.leadingLineLength;
		double w=associatedSymbol.getCurrentLabelWidth();
		return x+dx*r+(dx-1.0)*w/2.0;
	}

	@Override
	public double getRight() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double r=AircraftSymbol.aircraftSymbolSize/2+AircraftSymbol.leadingLineLength;
		double w=associatedSymbol.getCurrentLabelWidth();
		return x+dx*r+(dx+1.0)*w/2.0;
	}
}
