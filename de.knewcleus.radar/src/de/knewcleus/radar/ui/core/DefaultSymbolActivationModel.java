package de.knewcleus.radar.ui.core;

public class DefaultSymbolActivationModel extends AbstractSymbolActivationModel implements ISymbolActivationModel {
	protected boolean mouseover=false;
	protected boolean pressed=false;

	public boolean isMouseover() {
		return mouseover;
	}
	
	public boolean isPressed() {
		return pressed;
	}
	
	public void setMouseover(boolean mouseover) {
		this.mouseover = mouseover;
		setArmedForActivation(pressed||mouseover);
	}
	
	public void setPressed(boolean pressed) {
		if (!mouseover) {
			/* Ignore mouse presses, if the mouse is not inside the symbol */
			pressed=false;
		}
		this.pressed = pressed;
		setArmedForActivation(pressed||mouseover);
	}
}
