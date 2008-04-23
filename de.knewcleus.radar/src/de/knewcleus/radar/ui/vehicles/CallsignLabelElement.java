package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.vessels.Track;

public class CallsignLabelElement extends AbstractTextLabelElement {
	protected final Track track;
	
	public CallsignLabelElement(Track track) {
		this.track=track;
	}

	@Override
	protected String getText() {
		return getDisplayedCallsign();
	}
	
	protected String getDisplayedCallsign() {
		if (track.getAssociatedVessel()!=null) {
			return track.getAssociatedVessel().getCallsign();
		}
		if (!track.getSSRMode().hasSSRCode()) {
			return "****";
		}
		return "A"+track.getSSRCode();
	}
}
