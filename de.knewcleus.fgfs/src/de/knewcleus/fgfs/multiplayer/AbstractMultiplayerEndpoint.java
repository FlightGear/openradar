/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012 Wolfram Wagner
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

import static de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket.MAX_PACKET_SIZE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket;
import de.knewcleus.fgfs.multiplayer.protocol.XDRInputStream;
import de.knewcleus.fgfs.multiplayer.protocol.XDROutputStream;

public abstract class AbstractMultiplayerEndpoint<T extends Player> implements Runnable {
	protected static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final DatagramSocket datagramSocket;
	protected final IPlayerRegistry<T> playerRegistry;
	protected final List<IChatListener> chatListeners = Collections.synchronizedList(new ArrayList<IChatListener>());
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry<T> playerRegistry) throws IOException {
		this(playerRegistry,0);
	}
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry<T> playerRegistry, int port) throws IOException {
		if (port==0) {
			datagramSocket=new DatagramSocket();
		} else {
			datagramSocket=new DatagramSocket(port);
		}
		datagramSocket.setSoTimeout(0);//getUpdateMillis());
		this.playerRegistry=playerRegistry;
	}
	
	protected int getUpdateMillis() {
		return getPlayerTimeoutMillis();
	}
	
	protected int getPlayerTimeoutMillis() {
		return 30*60*1000;
	}
	
	public InetAddress getAddress() {
		return datagramSocket.getLocalAddress();
	}
	
	public int getPort() {
		return datagramSocket.getPort();
	}
	
	@Override
	public void run() {
		Thread myThread=Thread.currentThread();
		while (!myThread.isInterrupted()) {
			try {
				receivePacket();
			} catch (SocketTimeoutException e) {
				/* ignore */
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MultiplayerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			update();
		}
	}
	
	protected void update() {
		expirePlayers();
	}
	
	protected void receivePacket() throws IOException, MultiplayerException {
		byte[] buffer=new byte[MAX_PACKET_SIZE];
		final DatagramPacket packet=new DatagramPacket(buffer,MAX_PACKET_SIZE);
		datagramSocket.receive(packet);

		logger.finer("Received packet from "+packet.getAddress()+":"+packet.getPort()+", length="+packet.getLength());
		if (logger.isLoggable(Level.FINEST)) {
			int offset=0;
			while (offset<packet.getLength()) {
				String line="";
				final int end=Math.min(offset+16,packet.getLength());
				int i;
				for (i=offset;i<end;i++) {
					line+=String.format("%02x ",buffer[i]);
				}
				while (i<offset+16) {
					line+="   ";
				}
				for (i=offset;i<end;i++) {
					char ch=(char)buffer[i];
					if (Character.isISOControl(ch)) {
						ch='.';
					}
					line+=ch;
				}
				logger.finest(line);
				offset=end;
			}
		}

		ByteArrayInputStream byteInputStream;
		XDRInputStream xdrInputStream;
		byteInputStream=new ByteArrayInputStream(packet.getData(),packet.getOffset(),packet.getLength());
		xdrInputStream=new XDRInputStream(byteInputStream);
		
		MultiplayerPacket mppacket=MultiplayerPacket.decode(xdrInputStream);
		processPacket(packet.getAddress(), packet.getPort(), mppacket);
		xdrInputStream.close();
		byteInputStream.close();
	}
	
	protected void sendPacket(InetAddress address, int port, MultiplayerPacket mppacket) throws MultiplayerException {
		ByteArrayOutputStream byteOutputStream=new ByteArrayOutputStream(MAX_PACKET_SIZE);
		XDROutputStream xdrOutputStream=new XDROutputStream(byteOutputStream);
		mppacket.encode(xdrOutputStream);
		byte[] buffer=byteOutputStream.toByteArray();
		DatagramPacket packet=new DatagramPacket(buffer,buffer.length);
		packet.setAddress(address);
		packet.setPort(port);
		
		try {
			datagramSocket.send(packet);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}
	
	protected void sendPacket(Player player, MultiplayerPacket mppacket) throws MultiplayerException {
		sendPacket(player.getAddress(), player.getPort(), mppacket);
	}

	protected void processPacket(InetAddress address, int port, MultiplayerPacket mppacket) throws MultiplayerException {
		T player;
		final String callsign=mppacket.getCallsign();
		if(callsign.equals("*FGMS*")) return;
		synchronized (playerRegistry) {
			if (playerRegistry.hasPlayer(callsign)) {
				player=playerRegistry.getPlayer(callsign);
			} else {
				player=playerRegistry.createNewPlayer(mppacket.getCallsign());
				playerRegistry.registerPlayer(player);
				newPlayerLogon(player);
			}
		}
		player.setLastMessageTime(System.currentTimeMillis());
		player.setAddress(address);
		player.setPort(port);
		processPacket(player,mppacket);
	}
	
	protected void expirePlayers() {
		synchronized (playerRegistry) {
			Set<T> expiredPlayers=new HashSet<T>();
			long oldestExpireTime=System.currentTimeMillis()-getPlayerTimeoutMillis();
			for (T player: playerRegistry.getPlayers()) {
				if (player.getLastMessageTime()<=oldestExpireTime) {
					expiredPlayers.add(player);
				}
			}
			
			for (T player: expiredPlayers) {
				playerRegistry.unregisterPlayer(player);
			}
		}
	}
	
	public IPlayerRegistry<T> getPlayerRegistry() {
		return playerRegistry;
	}
	
	protected void newPlayerLogon(T player) throws MultiplayerException {
	}

	protected abstract void processPacket(T player, MultiplayerPacket mppacket) throws MultiplayerException;
	
	public void addChatListener(IChatListener l) {
	    chatListeners.add(l);
	}
    public void removeChatListener(IChatListener l) {
        chatListeners.remove(l);
    }
    protected void notifyChatListeners(String callSign, String frequency, String message) {
        for(IChatListener l : chatListeners) {
            l.newChatMessageReceived(callSign, frequency, message);
        }
    }
}
