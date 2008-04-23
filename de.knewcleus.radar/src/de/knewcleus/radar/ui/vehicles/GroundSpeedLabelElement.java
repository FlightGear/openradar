package de.knewcleus.radar.ui.vehicles;

import de.knewcleus.fgfs.Units;
import de.knewcleus.radar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.radar.vessels.Track;

public class GroundSpeedLabelElement extends AbstractTextLabelElement {
	protected final Track track;
	
	public GroundSpeedLabelElement(Track track) {
		this.track=track;
	}

	@Override
	protected String getText() {
		return String.format("%03d",(int)Math.round(track.getGroundSpeed()/Units.KNOTS/10.0));
	}

}
