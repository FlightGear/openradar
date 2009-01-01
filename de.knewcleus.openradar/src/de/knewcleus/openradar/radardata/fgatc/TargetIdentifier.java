package de.knewcleus.openradar.radardata.fgatc;

import java.net.InetAddress;

public class TargetIdentifier {
	protected final InetAddress address;
	protected final int port;
	
	public TargetIdentifier(InetAddress address, int port) {
		this.address=address;
		this.port=port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TargetIdentifier))
			return false;
		TargetIdentifier otherAddress=(TargetIdentifier)obj;
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
