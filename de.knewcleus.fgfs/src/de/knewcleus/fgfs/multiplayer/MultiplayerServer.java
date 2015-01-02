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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.protocol.ChatMessage;
import de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;

public class MultiplayerServer<T extends Player> extends AbstractMultiplayerEndpoint<T> {
	public static final int STANDARD_PORT=5000;
	public double maxDistance=100.0*Units.NM;
	protected List<MultiplayerPacket> queuedPackets=new ArrayList<MultiplayerPacket>();

	public MultiplayerServer(IPlayerRegistry<T> playerRegistry) throws IOException {
		super(playerRegistry,getStandardPort());
	}
	
	protected static int getStandardPort() {
		return Integer.getInteger("de.knewcleus.fgfs.multiplayer.server.port",STANDARD_PORT);
	}

	public MultiplayerServer(IPlayerRegistry<T> playerRegistry, int port) throws IOException {
		super(playerRegistry, port);
	}

	@Override
	protected void processPacket(T player, MultiplayerPacket mppacket) throws MultiplayerException {
		Position senderPosition;
		if (mppacket.getMessage() instanceof PositionMessage) {
			PositionMessage positionMessage=(PositionMessage)mppacket.getMessage();
			player.updatePosition(System.currentTimeMillis(),positionMessage);
			
			senderPosition=positionMessage.getPosition();
		} else {
			senderPosition=player.getCartesianPosition();
		}
		
		if (!(mppacket.getMessage() instanceof ChatMessage)) {
			/* Do not forward anything but chat messages from observers */
			if (mppacket.getCallsign().startsWith("obs"))
				return;
		}
		
		/* Send back to all local clients */
		synchronized (queuedPackets) {
			synchronized (playerRegistry) {
				for (T otherPlayer: playerRegistry.getPlayers()) {
					if (!player.isLocalPlayer())
						continue;
					
					for (MultiplayerPacket queuedPacket: queuedPackets)
						sendPacket(otherPlayer, queuedPacket);
					
					/* Don't send to sender itself */
					if (player==otherPlayer)
						continue;
					
					/* Don't send to players out of range */
					Position otherPos=otherPlayer.getCartesianPosition();
					Vector3D delta=otherPos.subtract(senderPosition);
					if (delta.getLength()>maxDistance)
						continue;
					
					sendPacket(otherPlayer, mppacket);
				}
			}
			queuedPackets.clear();
		}
		
		// TODO: send to relays
	}
	
	public void broadcastChatMessage(String text) {
		ChatMessage message=new ChatMessage("server:"+text);
		MultiplayerPacket packet=new MultiplayerPacket("*server*",message);
		
		synchronized (queuedPackets) {
			queuedPackets.add(packet);
		}
	}
	
	@Override
	protected void newPlayerLogon(T player) throws MultiplayerException {
		super.newPlayerLogon(player);
		
		ChatMessage message=new ChatMessage("server: Welcome to the FlightGear Multiplayer server at "+datagramSocket.getLocalSocketAddress()+":"+datagramSocket.getLocalPort());
		MultiplayerPacket packet=new MultiplayerPacket("*server*",message);
		sendPacket(player, packet);
		
		broadcastChatMessage("server:"+player.getCallsign()+" came online");
	}

    @Override
    protected int getPlayerTimeoutMillis() {
        return 1 * 60 * 1000;
    }
}
