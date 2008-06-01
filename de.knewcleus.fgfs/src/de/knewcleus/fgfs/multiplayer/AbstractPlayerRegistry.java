package de.knewcleus.fgfs.multiplayer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AbstractPlayerRegistry<T extends Player> implements IPlayerRegistry<T> {
	protected final static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final int playerExpirationTime = 15000;

	public abstract T createNewPlayer(String callsign) throws MultiplayerException;

	protected final Set<T> players = new HashSet<T>();
	protected final Map<String, T> playersByAddress = new HashMap<String, T>();

	public AbstractPlayerRegistry() {
		super();
	}

	public synchronized boolean hasPlayer(String callsign) {
		return playersByAddress.containsKey(callsign);
	}

	public synchronized T getPlayer(String callsign) {
		return playersByAddress.get(callsign);
	}

	public synchronized void registerPlayer(T player) {
		/* First unregister any old player at that address */
		if (hasPlayer(player.getCallsign()))
			unregisterPlayer(getPlayer(player.getCallsign()));
		players.add(player);
		playersByAddress.put(player.getCallsign(),player);
	}

	public synchronized void unregisterPlayer(T expiredPlayer) {
		players.remove(expiredPlayer);
		playersByAddress.remove(expiredPlayer.getAddress());
	}
	
	public Collection<T> getPlayers() {
		return Collections.unmodifiableCollection(players);
	}
}