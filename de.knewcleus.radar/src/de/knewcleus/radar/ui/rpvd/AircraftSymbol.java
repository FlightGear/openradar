package de.knewcleus.radar.ui.rpvd;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.aircraft.AircraftState;
import de.knewcleus.radar.aircraft.AircraftTaskState;
import de.knewcleus.radar.ui.Palette;
import de.knewcleus.radar.ui.vehicles.Aircraft;

public class AircraftSymbol extends AbstractVehicleSymbol implements IVehicleSymbol {
	protected static final float aircraftSymbolSize=6.0f;
	protected boolean inside=false;
	protected boolean pressed=false;
	
	public AircraftSymbol(RadarPlanViewContext radarPlanViewContext, Aircraft aircraft) {
		super(radarPlanViewContext, aircraft);
		label=new AircraftLabel(this);
	}
	
	@Override
	public Aircraft getVehicle() {
		return (Aircraft) super.getVehicle();
	}
	
	@Override
	protected void paintPositionSymbol(Graphics2D g2d, Point2D position) {
		Ellipse2D symbol=new Ellipse2D.Double(
				position.getX()-aircraftSymbolSize/2.0,position.getY()-aircraftSymbolSize/2.0,
				aircraftSymbolSize,aircraftSymbolSize);
		g2d.fill(symbol);
	}
	
	@Override
	protected Color getSymbolColor() {
		final AircraftState aircraftState=getVehicle().getAircraftState();
		if (aircraftState==null) {
			/* Not correlated */
			return Palette.BEACON;
		}
		AircraftTaskState aircraftTaskState=aircraftState.getTaskState();
		
		switch (aircraftTaskState) {
		case PENDING_IN:
		case PENDING:
		case ASSUMED:
			return Palette.BLACK;
		case NOT_CONCERNED:
		case ASSUMED_OUT:
		default:
			return Palette.BEACON;
		}
	}
	
	@Override
	public Rectangle2D getSymbolBounds() {
		final double w=aircraftSymbolSize;
		final double h=aircraftSymbolSize;
		return new Rectangle2D.Double(currentDevicePosition.getX()-w/2,currentDevicePosition.getY()-h/2,w,h);
	}
	
	@Override
	public Rectangle2D getBounds2D() {
		return getSymbolBounds();
	}
	
	@Override
	public boolean contains(int x, int y) {
		return containsPoint(x, y);
	}
	
	@Override
	public boolean containsPoint(double x, double y) {
		final Rectangle2D symbolBounds=getSymbolBounds();
		if (symbolBounds.contains(x,y))
			return true;
		final Rectangle2D labelBounds=getLabel().getBounds2D();
		return labelBounds.contains(x,y);
	}
	
	@Override
	public double getPriority() {
		return 1;
	}
}
