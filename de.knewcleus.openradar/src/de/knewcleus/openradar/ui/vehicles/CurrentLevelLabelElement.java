package de.knewcleus.openradar.ui.vehicles;

import de.knewcleus.openradar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.openradar.vessels.SSRMode;
import de.knewcleus.openradar.vessels.Track;

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
