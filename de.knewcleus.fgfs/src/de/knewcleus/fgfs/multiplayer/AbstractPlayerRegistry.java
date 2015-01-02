/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2015 Wolfram Wagner 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */
package de.knewcleus.fgfs.multiplayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


public abstract class AbstractPlayerRegistry<T extends Player> implements IPlayerRegistry<T> {
	protected static Logger log = LogManager.getLogger("de.knewcleus.fgfs.multiplayer");

	public abstract T createNewPlayer(String callsign) throws MultiplayerException;

	protected final Set<T> players = new HashSet<T>();
	protected final Map<String, T> playersByAddress = new HashMap<String, T>();
	protected final List<IPlayerListener<T>> listeners= new ArrayList<IPlayerListener<T>>();
	
	public AbstractPlayerRegistry() {
		super();
	}


    public void registerListener(IPlayerListener<T> l) {
        listeners.add(l);
    }

    public void unregisterListener(IPlayerListener<T> l) {
        listeners.remove(l);
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
		
		for(IPlayerListener<T> l : listeners) {
		    l.playerAdded(player);
		}
	}

	public synchronized void unregisterPlayer(T expiredPlayer) {
		players.remove(expiredPlayer);
		playersByAddress.remove(expiredPlayer.getCallsign());

		for(IPlayerListener<T> l : listeners) {
            l.playerRemoved(expiredPlayer);
        }
	}
	
	public Collection<T> getPlayers() {
		return Collections.unmodifiableCollection(players);
	}
}