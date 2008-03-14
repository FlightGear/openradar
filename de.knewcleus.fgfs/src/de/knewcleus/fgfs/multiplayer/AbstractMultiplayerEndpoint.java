package de.knewcleus.fgfs.multiplayer;

import static de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket.MAX_PACKET_SIZE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket;
import de.knewcleus.fgfs.multiplayer.protocol.XDRInputStream;
import de.knewcleus.fgfs.multiplayer.protocol.XDROutputStream;

public abstract class AbstractMultiplayerEndpoint<T extends Player> implements Runnable {
	protected static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final DatagramSocket datagramSocket;
	protected final IPlayerRegistry<T> playerRegistry;
	protected final int timeoutMillis=5000;
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry<T> playerRegistry) throws IOException {
		datagramSocket=new DatagramSocket();
		datagramSocket.setSoTimeout(timeoutMillis);
		this.playerRegistry=playerRegistry;
	}
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry<T> playerRegistry, int port) throws IOException {
		if (port==0) {
			datagramSocket=new DatagramSocket();
		} else {
			datagramSocket=new DatagramSocket(port);
		}
		datagramSocket.setSoTimeout(timeoutMillis);
		this.playerRegistry=playerRegistry;
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
			expirePlayers();
		}
	}
	
	protected void receivePacket() throws IOException, MultiplayerException {
		byte[] buffer=new byte[MAX_PACKET_SIZE];
		final DatagramPacket packet=new DatagramPacket(buffer,MAX_PACKET_SIZE);
		datagramSocket.receive(packet);

		ByteArrayInputStream byteInputStream;
		XDRInputStream xdrInputStream;
		byteInputStream=new ByteArrayInputStream(packet.getData(),packet.getOffset(),packet.getLength());
		xdrInputStream=new XDRInputStream(byteInputStream);
		
		MultiplayerPacket mppacket=MultiplayerPacket.decode(xdrInputStream);
		PlayerAddress address=new PlayerAddress(mppacket.getCallsign(),packet.getAddress(),packet.getPort());
		processPacket(address, mppacket);
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
	
	protected void sendPacket(PlayerAddress address, MultiplayerPacket mppacket) throws MultiplayerException {
		sendPacket(address.getAddress(), address.getPort(), mppacket);
	}

	protected synchronized void processPacket(PlayerAddress address, MultiplayerPacket mppacket) throws MultiplayerException {
		T player;
		synchronized (playerRegistry) {
			if (playerRegistry.hasPlayer(address)) {
				player=playerRegistry.getPlayer(address);
			} else {
				player=playerRegistry.createNewPlayer(address, mppacket.getCallsign());
				playerRegistry.registerPlayer(player);
				newPlayerLogon(player);
			}
		}
		player.setExpiryTime(System.currentTimeMillis()+timeoutMillis);
		processPacket(player,mppacket);
	}
	
	protected synchronized void expirePlayers() {
		synchronized (playerRegistry) {
			Set<T> expiredPlayers=new HashSet<T>();
			long currentTime=System.currentTimeMillis();
			for (T player: playerRegistry.getPlayers()) {
				if (player.getExpiryTime()<=currentTime) {
					expiredPlayers.add(player);
				}
			}
			
			for (T player: expiredPlayers) {
				playerRegistry.unregisterPlayer(player);
			}
		}
	}
	
	protected void newPlayerLogon(T player) throws MultiplayerException {
	}

	protected abstract void processPacket(T player, MultiplayerPacket mppacket) throws MultiplayerException;
}
