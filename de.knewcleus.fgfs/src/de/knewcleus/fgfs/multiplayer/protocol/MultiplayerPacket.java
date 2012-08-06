package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.IOException;

import de.knewcleus.fgfs.multiplayer.MultiplayerException;

public class MultiplayerPacket {
	public static final int MSG_MAGIC = 0x46474653; 	// "FGFS"
	public static final int RELAY_MAGIC = 0x53464746;	// "GSGF";
	public static final int PROTO_VER = 0x00010001; 	// 1.1

	public static final int MAX_PACKET_SIZE=2048; 
	// wwagner :changed from 1024 because they seem to have grown...
	// I tried to find valid information from Multiplayer server source, but I found no restriction yet
	// there is only a max line length, so this constraint seems to exist here only... 
	// the new value was big enough
	public static final int MAX_CALLSIGN_LEN=8;
	public static final int HEADER_SIZE=6*4+MAX_CALLSIGN_LEN;

	public static final int CHAT_MSG_ID=1;
	public static final int RESET_DATA_ID=6;
	public static final int POS_DATA_ID=7;
	
	protected final boolean isRelay;
	protected final String callsign;
	protected final IMultiplayerMessage message;
	
	public MultiplayerPacket(String callsign, IMultiplayerMessage message) {
		this(callsign,message,false);
	}
	
	public MultiplayerPacket(String callsign, IMultiplayerMessage message, boolean isRelay) {
		this.isRelay=isRelay;
		this.callsign=callsign;
		this.message=message;
	}
	
	public boolean isRelay() {
		return isRelay;
	}
	
	public String getCallsign() {
		return callsign;
	}
	
	public IMultiplayerMessage getMessage() {
		return message;
	}
	
	public int getLength() {
		return HEADER_SIZE+message.getMessageSize();
	}
	
	public void encode(XDROutputStream outputStream) throws MultiplayerException {
		if (message.getMessageSize()>IMultiplayerMessage.MAX_MESSAGE_SIZE)
			throw new MultiplayerException("Message is too large"); 
		try {
			outputStream.writeInt(MSG_MAGIC);
			outputStream.writeInt(PROTO_VER);
			outputStream.writeInt(message.getMessageID());
			outputStream.writeInt(getLength());
			outputStream.writeInt(0); 			// replyAddress is obsolete
			outputStream.writeInt(0); 	// replyPort is obsolete
			MPUtils.writeCString(outputStream, callsign, MAX_CALLSIGN_LEN);
			message.encode(outputStream);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}
	
	public static MultiplayerPacket decode(XDRInputStream inputStream) throws MultiplayerException {
		int magic, id;
		String callsign;
		try {
			magic = inputStream.readInt();

			if (magic!=MSG_MAGIC && magic!=RELAY_MAGIC) {
				throw new MultiplayerException("Invalid packet magic:"+Integer.toHexString(magic));
			}

			int version=inputStream.readInt();

			if (version!=PROTO_VER) {
				throw new MultiplayerException("Invalid protocol version:"+version);
			}

			id=inputStream.readInt();
			
			int msgLen=inputStream.readInt();
            
			if (msgLen<HEADER_SIZE || msgLen>MAX_PACKET_SIZE) {
				throw new MultiplayerException("Invalid packet size:"+msgLen);
			}
			
			inputStream.skip(8); // replyAddress and replyPort are obsolete
			
			callsign=MPUtils.readCString(inputStream, MAX_CALLSIGN_LEN);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
		
		IMultiplayerMessage message;
		
		switch (id) {
		case CHAT_MSG_ID:
			message=new ChatMessage();
			break;
		case POS_DATA_ID:
			message=new PositionMessage();
			break;
		default:
			throw new MultiplayerException("Unknown packet id "+id);
		}
		
		message.decode(inputStream);
		
		return new MultiplayerPacket(callsign, message, (magic==RELAY_MAGIC));
	}
	
	@Override
	public String toString() {
		return callsign+":"+message;
	}
}
