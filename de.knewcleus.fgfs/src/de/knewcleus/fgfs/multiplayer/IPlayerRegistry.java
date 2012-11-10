package de.knewcleus.fgfs.multiplayer;

import java.util.Collection;

public interface IPlayerRegistry<T extends Player> {

    public abstract void registerListener(IPlayerListener<T> l) throws MultiplayerException;

    public abstract void unregisterListener(IPlayerListener<T> l);
    
    
    public abstract boolean hasPlayer(String callsign);

	public abstract T getPlayer(String callsign);

	public abstract void registerPlayer(T player) throws MultiplayerException;
	
	public abstract void unregisterPlayer(T expiredPlayer);

    public abstract T createNewPlayer(String callsign) throws MultiplayerException;

	public abstract Collection<T> getPlayers();
}