package de.knewcleus.openradar.radio;

public interface IChannelListener {
	public void receive(Channel channel, IMessage message);
}
