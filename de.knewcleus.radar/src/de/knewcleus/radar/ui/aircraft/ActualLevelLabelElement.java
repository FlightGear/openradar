package de.knewcleus.radar.ui.aircraft;

import de.knewcleus.fgfs.Units;
import de.knewcleus.radar.aircraft.Target;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.ui.labels.ILabelDisplay;

public class ActualLevelLabelElement extends AbstractTextLabelElement {
	public ActualLevelLabelElement(ILabelDisplay labelDisplay, Target aircraftState) {
		super(labelDisplay, aircraftState);
	}

	@Override
	protected String getText() {
		return String.format("%03d",(int)Math.floor(aircraftState.getPressureAltitude()/Units.FT/100.0));
	}

}
