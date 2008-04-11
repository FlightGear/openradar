package de.knewcleus.radar.ui.rpvd;

import de.knewcleus.radar.ui.DefaultActivationModel;

public class VehicleLabelActivationModel extends DefaultActivationModel {
	@Override
	public boolean isActive() {
		return isMouseover() || isPressed();
	}
}
