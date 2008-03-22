package de.knewcleus.radar.ui.aircraft;

import de.knewcleus.fgfs.Units;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.ui.labels.ILabelDisplay;

public class ActualLevelLabelElement extends AbstractTextLabelElement {
	public ActualLevelLabelElement(ILabelDisplay labelDisplay, Aircraft aircraftState) {
		super(labelDisplay, aircraftState);
	}

	@Override
	protected String getText() {
		return String.format("%03d",(int)Math.floor(aircraft.getTarget().getPressureAltitude()/Units.FT/100.0));
	}

}
