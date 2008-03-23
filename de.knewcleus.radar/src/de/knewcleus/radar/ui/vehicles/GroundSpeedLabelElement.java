package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.fgfs.Units;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;

public class GroundSpeedLabelElement extends AbstractTextLabelElement {
	protected final Aircraft aircraft;
	
	public GroundSpeedLabelElement(Aircraft aircraft) {
		this.aircraft=aircraft;
	}

	@Override
	protected String getText() {
		return String.format("%03d",(int)Math.round(aircraft.getTrack().getGroundSpeed()/Units.KNOTS/10.0));
	}

}
