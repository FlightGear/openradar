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
package de.knewcleus.fgfs.multiplayer;

import java.net.InetAddress;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Quaternion;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;

public class Player {
	protected String callsign;
	protected InetAddress address;
	protected int port;
	protected long lastMessageTime;
	protected long lastPositionLocalTime;
	protected boolean isLocalPlayer=true;
	protected double positionTime;
	protected Position cartesianPosition=new Position();
	protected Quaternion orientation=Quaternion.one;
	protected Vector3D linearVelocity=new Vector3D();
	protected String model;
	protected String frequency="";
	
	public Player(String callsign) {
		this.callsign=callsign;
	}
	
	public void setAddress(InetAddress address) {
		this.address = address;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public int getPort() {
		return port;
	}
	
	public synchronized String getCallsign() {
		return callsign;
	}
	
    public synchronized void  getCallsign(String callsign) {
        this.callsign=callsign;
    }

    public synchronized void setLastMessageTime(long lastMessageTime) {
		this.lastMessageTime = lastMessageTime;
	}
	
	public synchronized long getLastMessageTime() {
		return lastMessageTime;
	}
	
	public synchronized double getPositionTime() {
		return positionTime;
	}
	
	public synchronized Position getCartesianPosition() {
		return cartesianPosition;
	}
	
	public synchronized String getModel() {
		return model;
	}
	
	public synchronized Vector3D getLinearVelocity() {
		return linearVelocity;
	}
	
	public synchronized boolean isLocalPlayer() {
		return isLocalPlayer;
	}
	
	public synchronized void setLocalPlayer(boolean isLocalPlayer) {
		this.isLocalPlayer = isLocalPlayer;
	}
	
	public synchronized void updatePosition(long t, PositionMessage packet) {
		lastPositionLocalTime=t;
		positionTime=packet.getTime();
		cartesianPosition=packet.getPosition();
		orientation=Quaternion.fromAngleAxis(packet.getOrientation());
		linearVelocity=packet.getLinearVelocity();
		model=packet.getModel();
		frequency = packet.getProperty("sim/multiplay/transmission-freq-hz");
	}
}
