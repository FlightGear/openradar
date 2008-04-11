package de.knewcleus.radar.ui.rpvd;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.knewcleus.radar.autolabel.ILabeledObject;
import de.knewcleus.radar.ui.IInteractiveSymbol;
import de.knewcleus.radar.ui.vehicles.IVehicle;

public interface IVehicleSymbol extends ILabeledObject, IInteractiveSymbol {
	
	public abstract IVehicle getVehicle();
	public abstract IVehicleLabel getLabel();
	
	public abstract void paintSymbol(Graphics2D g2d);
	public abstract void paintHeadingVector(Graphics2D g2d);
	public abstract void paintTrail(Graphics2D g2d);
	public abstract void paintLabel(Graphics2D g2d);

	public abstract Rectangle2D getSymbolBounds();
	
	public abstract void updatePosition();

}