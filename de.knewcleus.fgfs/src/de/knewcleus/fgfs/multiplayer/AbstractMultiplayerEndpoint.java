package de.knewcleus.fgfs.multiplayer;

import static de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket.MAX_PACKET_SIZE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;

import de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket;
import de.knewcleus.fgfs.multiplayer.protocol.XDRInputStream;
import de.knewcleus.fgfs.multiplayer.protocol.XDROutputStream;

public abstract class AbstractMultiplayerEndpoint extends Thread {
	protected static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final DatagramSocket datagramSocket;
	protected final IPlayerRegistry playerRegistry;
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry playerRegistry) throws MultiplayerException {
		try {
			datagramSocket=new DatagramSocket();
		} catch (SocketException e) {
			throw new MultiplayerException(e);
		}
		this.playerRegistry=playerRegistry;
	}
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry playerRegistry, int port) throws MultiplayerException {
		try {
			if (port==0)
					datagramSocket=new DatagramSocket();
			else
				datagramSocket=new DatagramSocket(port);
		} catch (SocketException e) {
			throw new MultiplayerException(e);
		}
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
		while (!isInterrupted()) {
			receivePacket();
		}
	}
	
	protected void receivePacket() {
		byte[] buffer=new byte[MAX_PACKET_SIZE];
		final DatagramPacket packet=new DatagramPacket(buffer,MAX_PACKET_SIZE);
		MultiplayerPacket mppacket;
		try {
			datagramSocket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		

		ByteArrayInputStream byteInputStream;
		XDRInputStream xdrInputStream;
		byteInputStream=new ByteArrayInputStream(packet.getData(),packet.getOffset(),packet.getLength());
		xdrInputStream=new XDRInputStream(byteInputStream);
		
		try {
			mppacket=MultiplayerPacket.decode(xdrInputStream);
			PlayerAddress address=new PlayerAddress(mppacket.getCallsign(),packet.getAddress(),packet.getPort());
			processPacket(address, mppacket);
		} catch (MultiplayerException e) {
			handleBadPacket(packet);
		}
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

	protected void processPacket(PlayerAddress address, MultiplayerPacket mppacket) throws MultiplayerException {
		playerRegistry.expirePlayers();
		Player player;
		if (playerRegistry.hasPlayer(address)) {
			player=playerRegistry.getPlayer(address);
		} else {
			player=playerRegistry.createNewPlayer(address, mppacket.getCallsign());
			playerRegistry.registerPlayer(player);
			newPlayerLogon(player);
		}
		player.setLastMessageTime(System.currentTimeMillis());
		processPacket(player,mppacket);
	}
	
	protected void newPlayerLogon(Player player) throws MultiplayerException {
	}

	protected abstract void processPacket(Player player, MultiplayerPacket mppacket) throws MultiplayerException;

	protected void handleBadPacket(DatagramPacket packet) {
	}
}
