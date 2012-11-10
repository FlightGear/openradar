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
