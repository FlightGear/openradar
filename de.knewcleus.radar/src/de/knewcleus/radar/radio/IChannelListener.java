package de.knewcleus.radar.radio;

public interface IChannelListener {
	public void receive(Channel channel, IMessage message);
}
