package de.knewcleus.fgfs.multiplayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AbstractPlayerRegistry<T extends Player> implements IPlayerRegistry<T> {
	protected final static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final int playerExpirationTime = 15000;

	public abstract T createNewPlayer(PlayerAddress address, String callsign);

	protected final Set<T> players = new HashSet<T>();
	protected final Map<PlayerAddress, T> playersByAddress = new HashMap<PlayerAddress, T>();

	public AbstractPlayerRegistry() {
		super();
	}

	public boolean hasPlayer(PlayerAddress address) {
		return playersByAddress.containsKey(address);
	}

	public T getPlayer(PlayerAddress address) {
		return playersByAddress.get(address);
	}

	public void registerPlayer(T player) {
		/* First unregister any old player at that address */
		if (hasPlayer(player.getAddress()))
			unregisterPlayer(getPlayer(player.getAddress()));
		players.add(player);
		playersByAddress.put(player.getAddress(),player);
	}

	public void expirePlayers() {
		Set<T> expiredPlayers=new HashSet<T>();
		for (T player: players) {
			if (playerExpired(player))
				expiredPlayers.add(player);
		}
		
		for (T expiredPlayer: expiredPlayers) {
			logger.info("Player "+expiredPlayer.getCallsign()+"@"+expiredPlayer.getAddress()+" expired");
			unregisterPlayer(expiredPlayer);
		}
	}

	public void unregisterPlayer(T expiredPlayer) {
		players.remove(expiredPlayer);
		playersByAddress.remove(expiredPlayer.getAddress());
	}

	public boolean playerExpired(T player) {
		return (player.getLastMessageTime()<System.currentTimeMillis()-playerExpirationTime);
	}
	
	public Collection<T> getPlayers() {
		return players;
	}

}