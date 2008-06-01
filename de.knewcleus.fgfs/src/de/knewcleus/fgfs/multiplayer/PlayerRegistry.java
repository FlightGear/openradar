package de.knewcleus.fgfs.multiplayer;


public class PlayerRegistry extends AbstractPlayerRegistry<Player> {
	@Override
	public Player createNewPlayer(String callsign) {
		return new Player(callsign);
	}
}
