package de.knewcleus.fgfs.multiplayer;


public class PlayerRegistry extends AbstractPlayerRegistry<Player> {
	@Override
	public Player createNewPlayer(PlayerAddress address, String callsign) {
		return new Player(address,callsign);
	}
}
