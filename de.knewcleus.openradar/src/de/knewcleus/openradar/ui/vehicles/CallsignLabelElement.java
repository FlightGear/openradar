package de.knewcleus.openradar.ui.vehicles;

import de.knewcleus.openradar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.openradar.vessels.Track;

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
		if (track.getCorrelatedCallsign()!=null) {
			return track.getCorrelatedCallsign();
		}
		if (!track.getSSRMode().hasSSRCode()) {
			return "****";
		}
		return "A"+track.getSSRCode();
	}
}
