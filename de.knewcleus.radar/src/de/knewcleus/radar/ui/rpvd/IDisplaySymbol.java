package de.knewcleus.radar.ui.rpvd;

import java.awt.geom.Rectangle2D;

public interface IDisplaySymbol {
	public abstract Rectangle2D getBounds2D();
	public abstract boolean containsPoint(double x, double y);
	public abstract void updatePosition();
	
	public abstract void setInside(boolean inside);
	public abstract void setPressed(boolean pressed);
	public abstract boolean isInside();
	public abstract boolean isPressed();
	
	public abstract boolean isActive();
}