package de.knewcleus.radar.ui.rpvd;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import de.knewcleus.radar.autolabel.Label;

public interface IVehicleLabel extends Label {
	public abstract void updateLabelContents();
	public abstract IVehicleSymbol getVehicleSymbol();
	public abstract void processMouseEvent(MouseEvent e);
	public Point2D getHookPosition();
}
