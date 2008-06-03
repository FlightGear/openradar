package de.knewcleus.openradar.radio;

public class Transceiver implements IChannelListener {
	protected final IEndpoint endpoint;
	protected Channel tunedChannel;
	
	public Transceiver(IEndpoint endpoint) {
		this.endpoint=endpoint;
	}
	
	public void tune(Channel channel) {
		if (tunedChannel!=null)
			tunedChannel.removeListener(this);
		tunedChannel=channel;
		if (tunedChannel!=null)
			tunedChannel.addListener(this);
	}
	
	public Channel getTunedChannel() {
		return tunedChannel;
	}
	
	public void receive(Channel channel, IMessage message) {
		endpoint.receive(channel, message);
	}
	
	public void transmit(IMessage message) {
		if (tunedChannel!=null)
			tunedChannel.transmit(message);
	}
}
