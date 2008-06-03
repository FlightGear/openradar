package de.knewcleus.openradar.radio;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Message implements IMessage {
	protected final MessageType messageType;
	protected final Set<IEndpoint> recipients;
	protected final IEndpoint sender;
	protected final Map<String, Object> properties=new HashMap<String, Object>();
	
	public Message(MessageType messageType, IEndpoint sender, IEndpoint recipient) {
		this.messageType=messageType;
		this.sender=sender;
		this.recipients=Collections.singleton(recipient);
	}
	
	public Message(MessageType messageType, IEndpoint sender, Set<IEndpoint> recipients) {
		this.messageType=messageType;
		this.sender=sender;
		this.recipients=recipients;
	}

	public MessageType getMessageType() {
		return messageType;
	}
	
	public IEndpoint getSender() {
		return sender;
	}
	
	public Set<IEndpoint> getRecipients() {
		return recipients;
	}
	
	public boolean isRecipient(IEndpoint endpoint) {
		return recipients.contains(endpoint);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String name) {
		return (T)properties.get(name);
	}
	
	public <T> void setProperty(String name, T value) {
		properties.put(name, value);
	}
	
	public boolean hasProperty(String name) {
		return properties.containsKey(name);
	}
}
