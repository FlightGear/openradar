package de.knewcleus.radar.radio;

import java.util.HashSet;
import java.util.Set;

public class Channel {
	protected Set<IChannelListener> listeners=new HashSet<IChannelListener>();
	
	public void transmit(IMessage message) {
		for (IChannelListener listener: listeners) {
			if (listener==message.getSender())
				continue;
			listener.receive(this, message);
		}
	}
	
	public void addListener(IChannelListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IChannelListener listener) {
		listeners.remove(listener);
	}
}
