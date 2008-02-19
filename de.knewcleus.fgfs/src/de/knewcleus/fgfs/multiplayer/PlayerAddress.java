package de.knewcleus.fgfs.multiplayer;

import java.net.InetAddress;

public class PlayerAddress {
	protected final String callsign;
	protected final InetAddress address;
	protected final int port;
	
	public PlayerAddress(String callsign, InetAddress address, int port) {
		this.callsign=callsign;
		this.address=address;
		this.port=port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PlayerAddress))
			return false;
		PlayerAddress other=(PlayerAddress)obj;
		return other.callsign.equals(callsign) && 
			other.address.equals(address) &&
			other.port==port;
	}
	
	@Override
	public int hashCode() {
		return callsign.hashCode()^address.hashCode()^port;
	}
	
	public String getCallsign() {
		return callsign;
	}
	
	public InetAddress getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public String toString() {
		return callsign+"@"+address.toString()+":"+port;
	}
}
