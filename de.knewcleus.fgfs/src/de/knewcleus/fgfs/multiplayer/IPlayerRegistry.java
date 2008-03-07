package de.knewcleus.fgfs.multiplayer;

import java.util.Collection;

public interface IPlayerRegistry<T extends Player> {

	public abstract boolean hasPlayer(PlayerAddress address);

	public abstract T getPlayer(PlayerAddress address);

	public abstract void registerPlayer(T player);

	public abstract T createNewPlayer(PlayerAddress address,
			String callsign);

	public abstract void expirePlayers();
	
	public abstract Collection<T> getPlayers();

}