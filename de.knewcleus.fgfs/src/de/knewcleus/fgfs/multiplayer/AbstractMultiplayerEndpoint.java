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
	protected List<IChatListener> chatListeners = new ArrayList<IChatListener>();
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry<T> playerRegistry) throws IOException {
		datagramSocket=new DatagramSocket();
		datagramSocket.setSoTimeout(getUpdateMillis());
		this.playerRegistry=playerRegistry;
	}
	
	public AbstractMultiplayerEndpoint(IPlayerRegistry<T> playerRegistry, int port) throws IOException {
		if (port==0) {
			datagramSocket=new DatagramSocket();
		} else {
			datagramSocket=new DatagramSocket(port);
		}
		datagramSocket.setSoTimeout(getUpdateMillis());
		this.playerRegistry=playerRegistry;
	}
	
	protected int getUpdateMillis() {
		return getPlayerTimeoutMillis();
	}
	
	protected int getPlayerTimeoutMillis() {
		return 15000;
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
