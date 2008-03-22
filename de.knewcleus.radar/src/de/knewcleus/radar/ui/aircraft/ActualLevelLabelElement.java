package de.knewcleus.radar.ui.aircraft;

import de.knewcleus.fgfs.Units;
import de.knewcleus.radar.aircraft.SSRMode;
import de.knewcleus.radar.aircraft.Target;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.ui.labels.ILabelDisplay;

public class ActualLevelLabelElement extends AbstractTextLabelElement {
	public ActualLevelLabelElement(ILabelDisplay labelDisplay, Aircraft aircraftState) {
		super(labelDisplay, aircraftState);
	}

	@Override
	protected String getText() {
		final Target target=aircraft.getTarget();
		final SSRMode ssrMode=target.getSSRMode();
		
		if (!ssrMode.hasAltitudeEncoding())
			return "";
		return String.format("%03d",(int)Math.floor(target.getPressureAltitude()/Units.FT/100.0));
	}

}
