package de.knewcleus.fgfs.multiplayer;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Quaternion;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;

public class Player {
	protected final PlayerAddress address;
	protected final String callsign;
	protected long expiryTime;
	protected long lastPositionTime;
	protected boolean isLocalPlayer=true;
	protected Position cartesianPosition=new Position();
	protected Quaternion orientation=Quaternion.one;
	protected Vector3D linearVelocity=new Vector3D();
	protected String model;
	
	public Player(PlayerAddress address, String callsign) {
		this.address=address;
		this.callsign=callsign;
	}
	
	public PlayerAddress getAddress() {
		return address;
	}
	
	public String getCallsign() {
		return callsign;
	}
	
	public void setExpiryTime(long lastMessageTime) {
		this.expiryTime = lastMessageTime;
	}
	
	public long getExpiryTime() {
		return expiryTime;
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
		lastPositionTime=t;
		cartesianPosition=packet.getPosition();
		orientation=Quaternion.fromAngleAxis(packet.getOrientation());
		linearVelocity=packet.getLinearVelocity();
		model=packet.getModel();
	}
}
