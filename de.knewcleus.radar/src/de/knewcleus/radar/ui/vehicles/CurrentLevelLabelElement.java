package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.vessels.SSRMode;
import de.knewcleus.radar.vessels.Track;

public class CurrentLevelLabelElement extends AbstractTextLabelElement {
	protected final Track track;
	
	public CurrentLevelLabelElement(Track track) {
		this.track=track;
	}

	@Override
	protected String getText() {
		final SSRMode ssrMode=track.getSSRMode();
		
		if (!ssrMode.hasAltitudeEncoding())
			return "";
		return String.format("%03d",track.getFlightLevel());
	}

}
