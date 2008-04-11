package de.knewcleus.radar.ui;

public class DefaultActivationModel {
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
	}
	
	public void setPressed(boolean pressed) {
		if (!mouseover) {
			pressed=false;
		}
		this.pressed = pressed;
	}
	
	public boolean isActive() {
		return mouseover;
	}
}
