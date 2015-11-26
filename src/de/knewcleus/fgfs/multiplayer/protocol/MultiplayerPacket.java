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
package de.knewcleus.fgfs.multiplayer.protocol;

import java.io.IOException;

import de.knewcleus.fgfs.multiplayer.MultiplayerException;

public class MultiplayerPacket {
	public static final int MSG_MAGIC = 0x46474653; 	// "FGFS"
	public static final int RELAY_MAGIC = 0x53464746;	// "GSGF";
	public static final int PROTO_VER = 0x00010001; 	// 1.1

	public static final int MAX_PACKET_SIZE=2048;
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
			outputStream.writeInt(0); 	// replyAddress is obsolete
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
