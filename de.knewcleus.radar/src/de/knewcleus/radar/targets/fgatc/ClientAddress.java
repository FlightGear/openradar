package de.knewcleus.radar.targets.fgatc;

import java.net.InetAddress;

public class ClientAddress {
	protected final InetAddress address;
	protected final int port;
	
	public ClientAddress(InetAddress address, int port) {
		this.address=address;
		this.port=port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ClientAddress))
			return false;
		ClientAddress otherAddress=(ClientAddress)obj;
		return otherAddress.address.equals(address) && otherAddress.port==port;
	}
	
	@Override
	public int hashCode() {
		return address.hashCode()*37+port;
	}
	
	@Override
	public String toString() {
		return address.toString()+":"+port;
	}
}
