package de.knewcleus.radar.ui.rpvd;

import de.knewcleus.radar.autolabel.Label;
import de.knewcleus.radar.autolabel.LabeledObject;

public class AircraftLabel implements Label {
	protected final AircraftSymbol associatedSymbol;
	protected double hookX,hookY;
	protected double centerX,centerY;
	
	public AircraftLabel(AircraftSymbol associatedSymbol) {
		this.associatedSymbol=associatedSymbol;
		hookX=1.0;
		hookY=1.0;
	}

	@Override
	public LabeledObject getAssociatedObject() {
		return associatedSymbol;
	}
	
	@Override
	public double getChargeDensity() {
		return 1;
	}
	
	@Override
	public double getHookX() {
		return hookX;
	}
	
	@Override
	public double getHookY() {
		return hookY;
	}
	
	public boolean containsPosition(double x, double y) {
		if (getLeft()<=x && x<=getRight() && getTop()<=y && y<=getBottom())
			return true;
		return false;
	}
	
	@Override
	public void move(double dx, double dy) {
		hookX+=dx;
		hookY+=dy;
		
		final double len=Math.sqrt(hookX*hookX+hookY*hookY);
		final double dirX,dirY;
		
		dirX=hookX/len;
		dirY=hookY/len;
		
		if (len<AircraftSymbol.minLabelDist) {
			hookX=dirX*AircraftSymbol.minLabelDist;
			hookY=dirY*AircraftSymbol.minLabelDist;
		} else if (len>AircraftSymbol.maxLabelDist) {
			hookX=dirX*AircraftSymbol.maxLabelDist;
			hookY=dirY*AircraftSymbol.maxLabelDist;
		}
		
		centerX=hookX+dirX*associatedSymbol.getCurrentLabelWidth()/2.0;
		centerY=hookY+dirY*associatedSymbol.getCurrentLabelHeight()/2.0;
	}

	@Override
	public double getTop() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double h=associatedSymbol.getCurrentLabelHeight();
		
		return y+centerY-h/2.0;
	}

	@Override
	public double getBottom() {
		double y=associatedSymbol.getCurrentDevicePosition().getY();
		double h=associatedSymbol.getCurrentLabelHeight();
		
		return y+centerY+h/2.0;
	}

	@Override
	public double getLeft() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double w=associatedSymbol.getCurrentLabelWidth();
		
		return x+centerX-w/2.0;
	}

	@Override
	public double getRight() {
		double x=associatedSymbol.getCurrentDevicePosition().getX();
		double w=associatedSymbol.getCurrentLabelWidth();
		
		return x+centerX+w/2.0;
	}
	
	@Override
	public String toString() {
		return "cx="+centerX+" cy="+centerY+" hookX="+hookX+" hookY="+hookY;
	}
}
