package de.knewcleus.fgfs.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;

public abstract class MultiplayerClient<T extends Player> extends AbstractMultiplayerEndpoint<T> {
	protected static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final InetAddress serverAddress;
	protected final int serverPort;
	protected final Queue<String> chatQueue=new ArrayDeque<String>();
	protected String lastChatMessage="";
	
	protected long lastPositionUpdateTimeMillis;
	
	protected final int interPositionUpdateTimeMillis = 1000;
	
	public final static String clientPortKey="clientPort";
	public final static String serverHostKey="serverHost";
	public final static String serverPortKey="serverPort";
	
	public MultiplayerClient(IPlayerRegistry<T> playerRegistry) throws IOException {
		super(playerRegistry, getStandardClientPort());
		serverAddress=InetAddress.getByName(getStandardServerHost());
		serverPort=getStandardServerPort();
		lastPositionUpdateTimeMillis=System.currentTimeMillis();
	}
	
	public static Preferences getPreferences() {
		return Preferences.userNodeForPackage(MultiplayerClient.class);
	}
	
	public static String getStandardServerHost() {
		final Preferences preferences=getPreferences();
		return preferences.get(serverHostKey, "localhost");
	}
	
	public static int getStandardServerPort() {
		final Preferences preferences=getPreferences();
		return preferences.getInt(serverPortKey, MultiplayerServer.STANDARD_PORT);
	}
	
	public static int getStandardClientPort() {
		final Preferences preferences=getPreferences();
		return preferences.getInt(clientPortKey, 0);
	}
	
	@Override
	protected void processPacket(T player, MultiplayerPacket mppacket) throws MultiplayerException {
		if (mppacket.getMessage() instanceof PositionMessage) {
			PositionMessage positionMessage=(PositionMessage)mppacket.getMessage();
			player.updatePosition(System.currentTimeMillis(),positionMessage);
		}
	}
	
	public void sendChatMessage(String message) {
		chatQueue.add(message);
	}
	
	public void sendPacket(MultiplayerPacket mppacket) throws MultiplayerException {
		sendPacket(serverAddress, serverPort, mppacket);
	}
	
	@Override
	protected void update() {
		super.update();
		sendPositionUpdate();
	}
	
	protected void sendPositionUpdate() {
		if (lastPositionUpdateTimeMillis+interPositionUpdateTimeMillis>System.currentTimeMillis()) {
			return;
		}
		PositionMessage positionMessage=new PositionMessage();
		positionMessage.setModel(getModel());
		positionMessage.setPosition(getPosition());
		positionMessage.setOrientation(getOrientation());
		positionMessage.setLinearVelocity(getLinearVelocity());
		// TODO: update the rest
		
		if (chatQueue.size()>0) {
			lastChatMessage=chatQueue.remove();
		}
		positionMessage.putProperty("sim/multiplay/chat", lastChatMessage);
		
		MultiplayerPacket mppacket=new MultiplayerPacket(getCallsign(), positionMessage);
		try {
			sendPacket(serverAddress, serverPort, mppacket);
		} catch (MultiplayerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lastPositionUpdateTimeMillis=System.currentTimeMillis();
	}
	
	public abstract String getModel();
	
	public abstract String getCallsign();
	
	public abstract Position getPosition();
	
	public abstract Position getOrientation();
	
	public abstract Position getLinearVelocity();
}
