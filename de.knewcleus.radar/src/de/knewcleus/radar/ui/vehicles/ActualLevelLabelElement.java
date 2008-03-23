package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.fgfs.Units;
import de.knewcleus.radar.targets.SSRMode;
import de.knewcleus.radar.targets.Track;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;

public class ActualLevelLabelElement extends AbstractTextLabelElement {
	protected final Aircraft aircraft;
	
	public ActualLevelLabelElement(Aircraft aircraft) {
		this.aircraft=aircraft;
	}

	@Override
	protected String getText() {
		final Track target=aircraft.getTrack();
		final SSRMode ssrMode=target.getSSRMode();
		
		if (!ssrMode.hasAltitudeEncoding())
			return "";
		return String.format("%03d",(int)Math.floor(target.getPressureAltitude()/Units.FT/100.0));
	}

}
