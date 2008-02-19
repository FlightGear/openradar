package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.IOException;

import de.knewcleus.fgfs.multiplayer.MultiplayerException;

public class ChatMessage implements IMultiplayerMessage {
	public static final int MAX_CHAT_MSG_LEN=256;
	
	protected String message;

	public ChatMessage() {
	}
	
	public ChatMessage(String message) {
		this.message=message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
	
	public int getMessageID() {
		return MultiplayerPacket.CHAT_MSG_ID;
	}
	
	public int getMessageSize() {
		return MAX_CHAT_MSG_LEN;
	}
	
	public void encode(XDROutputStream outputStream) throws MultiplayerException {
		try {
			MPUtils.writeCString(outputStream, message, MAX_CHAT_MSG_LEN);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}

	public void decode(XDRInputStream inputStream) throws MultiplayerException {
		try {
			message=MPUtils.readCString(inputStream, MAX_CHAT_MSG_LEN);
		} catch (IOException e) {
			throw new MultiplayerException(e);
		}
	}
	
	@Override
	public String toString() {
		return message;
	}
}
