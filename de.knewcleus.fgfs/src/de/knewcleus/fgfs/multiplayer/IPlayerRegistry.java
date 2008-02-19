package de.knewcleus.fgfs.multiplayer;

import java.util.Collection;

public interface IPlayerRegistry {

	public abstract boolean hasPlayer(PlayerAddress address);

	public abstract Player getPlayer(PlayerAddress address);

	public abstract void registerPlayer(Player player);

	public abstract Player createNewPlayer(PlayerAddress address,
			String callsign);

	public abstract void expirePlayers();
	
	public abstract Collection<Player> getPlayers();

}