package de.knewcleus.fgfs.multiplayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AbstractPlayerRegistry implements IPlayerRegistry {
	protected final static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final int playerExpirationTime = 15000;

	public abstract Player createNewPlayer(PlayerAddress address, String callsign);

	protected final Set<Player> players = new HashSet<Player>();
	protected final Map<PlayerAddress, Player> playersByAddress = new HashMap<PlayerAddress, Player>();

	public AbstractPlayerRegistry() {
		super();
	}

	public boolean hasPlayer(PlayerAddress address) {
		return playersByAddress.containsKey(address);
	}

	public Player getPlayer(PlayerAddress address) {
		return playersByAddress.get(address);
	}

	public void registerPlayer(Player player) {
		/* First unregister any old player at that address */
		if (hasPlayer(player.getAddress()))
			unregisterPlayer(getPlayer(player.getAddress()));
		players.add(player);
		playersByAddress.put(player.getAddress(),player);
	}

	public void expirePlayers() {
		Set<Player> expiredPlayers=new HashSet<Player>();
		for (Player player: players) {
			if (playerExpired(player))
				expiredPlayers.add(player);
		}
		
		for (Player expiredPlayer: expiredPlayers) {
			logger.info("Player "+expiredPlayer.getCallsign()+"@"+expiredPlayer.getAddress()+" expired");
			unregisterPlayer(expiredPlayer);
		}
	}

	public void unregisterPlayer(Player expiredPlayer) {
		players.remove(expiredPlayer);
		playersByAddress.remove(expiredPlayer.getAddress());
	}

	public boolean playerExpired(Player player) {
		return (player.getLastMessageTime()<System.currentTimeMillis()-playerExpirationTime);
	}
	
	public Collection<Player> getPlayers() {
		return players;
	}

}