package de.knewcleus.radar.ui.rpvd;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.autolabel.LabeledObject;
import de.knewcleus.radar.ui.vehicles.IVehicle;

public interface IVehicleSymbol extends LabeledObject, IDisplaySymbol {
	
	public abstract IVehicle getVehicle();
	public abstract IVehicleLabel getLabel();
	
	public abstract void paintSymbol(Graphics2D g2d);
	public abstract void paintHeadingVector(Graphics2D g2d);
	public abstract void paintTrail(Graphics2D g2d);
	public abstract void paintLabel(Graphics2D g2d);

	public abstract Rectangle2D getSymbolBounds();
	public abstract boolean canSelect();

	public abstract boolean isLocked();
	public abstract void setLocked(boolean isLocked);

}