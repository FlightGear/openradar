package de.knewcleus.openradar.ui.vehicles;

import de.knewcleus.fgfs.Units;
import de.knewcleus.openradar.ui.labels.AbstractTextLabelElement;
import de.knewcleus.openradar.vessels.Track;

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
