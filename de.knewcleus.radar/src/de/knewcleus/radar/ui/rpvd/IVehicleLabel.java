package de.knewcleus.radar.ui.rpvd;

import java.awt.event.MouseEvent;

import de.knewcleus.radar.autolabel.Label;

public interface IVehicleLabel extends Label {
	public abstract void updatePosition();
	public abstract void updateLabelContents();
	public abstract IVehicleSymbol getVehicleSymbol();
	public abstract void setInitialHookPosition(double dx, double dy);
	public abstract void setHookPosition(double dx, double dy);
	public abstract void processMouseEvent(MouseEvent e);
}
