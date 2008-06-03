package de.knewcleus.radar.ui.core;

import javax.swing.event.ChangeListener;

public interface ISymbolActivationModel {
	public abstract boolean isMouseover();

	public abstract boolean isPressed();

	public abstract void setMouseover(boolean mouseover);

	public abstract void setPressed(boolean pressed);

	public abstract boolean isArmedForActivation();
	
	public void registerChangeListener(ChangeListener changeListener);
	
	public void unregisterChangeListener(ChangeListener changeListener);
}