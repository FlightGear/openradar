package de.knewcleus.fgfs.multiplayer.protocol;

import static de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket.HEADER_SIZE;
import static de.knewcleus.fgfs.multiplayer.protocol.MultiplayerPacket.MAX_PACKET_SIZE;
import de.knewcleus.fgfs.multiplayer.MultiplayerException;

public interface IMultiplayerMessage {
	public final static int MAX_MESSAGE_SIZE=MAX_PACKET_SIZE-HEADER_SIZE;
	public void encode(XDROutputStream outputStream) throws MultiplayerException;
	public void decode(XDRInputStream inputStream) throws MultiplayerException;
	
	public int getMessageID();
	public int getMessageSize();
}
