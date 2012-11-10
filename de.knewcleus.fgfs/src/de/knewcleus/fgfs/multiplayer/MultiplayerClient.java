package de.knewcleus.fgfs.multiplayer;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.logging.Logger;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;

public abstract class MultiplayerClient<T extends Player> extends AbstractMultiplayerEndpoint<T> {
	protected static Logger logger=Logger.getLogger("de.knewcleus.fgfs.multiplayer");
	protected final InetAddress serverAddress;
	protected final int serverPort;
	protected final int localPort;
	protected final Queue<String> chatQueue=new ArrayDeque<String>();
	protected String lastChatMessage="";
	
	protected long lastPositionUpdateTimeMillis;
	
	protected final int interPositionUpdateTimeMillis = 1000;
	
	public MultiplayerClient(IPlayerRegistry<T> playerRegistry, String mpServer, int mpServerPort, int mpLocalPort) throws IOException {
		super(playerRegistry, mpLocalPort);
		this.serverAddress=InetAddress.getByName(mpServer);
		this.serverPort=mpServerPort;
		this.localPort = mpLocalPort;
		lastPositionUpdateTimeMillis=System.currentTimeMillis();
	}
	
	@Override
	protected void processPacket(T player, MultiplayerPacket mppacket) throws MultiplayerException {
		if (mppacket.getMessage() instanceof PositionMessage) {
			PositionMessage positionMessage=(PositionMessage)mppacket.getMessage();
			String chatMessage = positionMessage.getProperty("sim/multiplay/chat");
			if(chatMessage!=null && !chatMessage.isEmpty()) {
			    if(!"hello".equalsIgnoreCase(chatMessage)) {
			        BigDecimal bdFreq = new BigDecimal((String)positionMessage.getProperty("sim/multiplay/transmission-freq-hz"));
			        bdFreq = bdFreq.divide(new BigDecimal(1000000));
			        String frequency = bdFreq.toPlainString();
			        notifyChatListeners(mppacket.getCallsign(), frequency, chatMessage);
			    }
			}
			//System.out.println(positionMessage.getModel());
			player.updatePosition(System.currentTimeMillis(),positionMessage);
		} else {
		    // MP Server messages deprecated
		    // ChatMessage chatMessage = (ChatMessage)mppacket.getMessage();
		    // notifyChatListeners(mppacket.getCallsign(), chatMessage.getMessage());
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
