package de.knewcleus.fgfs.multiplayer;

import java.net.InetAddress;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Quaternion;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;

public class Player {
	protected final String callsign;
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
	
	public String getCallsign() {
		return callsign;
	}
	
	public void setLastMessageTime(long lastMessageTime) {
		this.lastMessageTime = lastMessageTime;
	}
	
	public long getLastMessageTime() {
		return lastMessageTime;
	}
	
	public double getPositionTime() {
		return positionTime;
	}
	
	public Position getCartesianPosition() {
		return cartesianPosition;
	}
	
	public String getModel() {
		return model;
	}
	
	public Vector3D getLinearVelocity() {
		return linearVelocity;
	}
	
	public boolean isLocalPlayer() {
		return isLocalPlayer;
	}
	
	public void setLocalPlayer(boolean isLocalPlayer) {
		this.isLocalPlayer = isLocalPlayer;
	}
	
	public void updatePosition(long t, PositionMessage packet) {
		lastPositionLocalTime=t;
		positionTime=packet.getTime();
		cartesianPosition=packet.getPosition();
		orientation=Quaternion.fromAngleAxis(packet.getOrientation());
		linearVelocity=packet.getLinearVelocity();
		model=packet.getModel();
	}
}
