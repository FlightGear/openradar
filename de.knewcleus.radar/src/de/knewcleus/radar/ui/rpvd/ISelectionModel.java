package de.knewcleus.radar.ui.rpvd;

public interface ISelectionModel {
	public abstract void setInside(boolean inside);
	public abstract void setPressed(boolean pressed);
	public abstract boolean isInside();
	public abstract boolean isPressed();
	
	public abstract boolean isActive();
}
