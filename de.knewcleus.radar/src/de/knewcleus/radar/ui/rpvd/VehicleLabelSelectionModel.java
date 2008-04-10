package de.knewcleus.radar.ui.rpvd;

public class VehicleLabelSelectionModel implements ISelectionModel {
	protected boolean inside=false;
	protected boolean pressed=false;

	@Override
	public boolean isActive() {
		return inside || pressed;
	}

	@Override
	public boolean isInside() {
		return inside;
	}

	@Override
	public boolean isPressed() {
		return pressed;
	}

	@Override
	public void setInside(boolean inside) {
		if (inside==this.inside)
			return;
		this.inside=inside;
	}

	@Override
	public void setPressed(boolean pressed) {
		if (pressed==this.pressed)
			return;
		if (!inside)
			pressed=false;
		this.pressed=pressed;
	}

}
