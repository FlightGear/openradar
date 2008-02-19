package de.knewcleus.radar.radio;

import java.util.Set;

public interface IMessage {

	public abstract MessageType getMessageType();

	public abstract IEndpoint getSender();

	public abstract Set<IEndpoint> getRecipients();

	public abstract boolean isRecipient(IEndpoint endpoint);

	@SuppressWarnings("unchecked")
	public abstract <T> T getProperty(String name);

	public abstract <T> void setProperty(String name, T value);

	public abstract boolean hasProperty(String name);

}