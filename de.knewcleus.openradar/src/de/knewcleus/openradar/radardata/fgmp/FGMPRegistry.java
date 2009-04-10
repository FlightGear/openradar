package de.knewcleus.openradar.radardata.fgmp;

import de.knewcleus.fgfs.multiplayer.AbstractPlayerRegistry;

public class FGMPRegistry extends AbstractPlayerRegistry<TargetStatus> {
	@Override
	public TargetStatus createNewPlayer(String callsign) {
		return new TargetStatus(callsign);
	}
}
