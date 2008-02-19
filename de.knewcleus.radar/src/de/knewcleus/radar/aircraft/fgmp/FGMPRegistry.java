package de.knewcleus.radar.aircraft.fgmp;

import de.knewcleus.fgfs.multiplayer.AbstractPlayerRegistry;
import de.knewcleus.fgfs.multiplayer.Player;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.radar.Scenario;

public class FGMPRegistry extends AbstractPlayerRegistry {
	protected final Scenario scenario;

	public FGMPRegistry(Scenario scenario) {
		this.scenario=scenario;
	}

	@Override
	public Player createNewPlayer(PlayerAddress address, String callsign) {
		FGMPAircraft aircraft=new FGMPAircraft(address,callsign);
		scenario.addAircraft(aircraft);
		return aircraft;
	}
	
	@Override
	public void unregisterPlayer(Player expiredPlayer) {
		super.unregisterPlayer(expiredPlayer);
		scenario.removeAircraft((FGMPAircraft)expiredPlayer);
	}
}
