/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
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
