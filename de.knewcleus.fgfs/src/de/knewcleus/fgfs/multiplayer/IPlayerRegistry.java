package de.knewcleus.fgfs.multiplayer;

import java.util.Collection;

public interface IPlayerRegistry<T extends Player> {

	public abstract boolean hasPlayer(PlayerAddress address);

	public abstract T getPlayer(PlayerAddress address);

	public abstract void registerPlayer(T player) throws MultiplayerException;
	
	public abstract void unregisterPlayer(T expiredPlayer);

	public abstract T createNewPlayer(PlayerAddress address, String callsign) throws MultiplayerException;

	public abstract Collection<T> getPlayers();
}