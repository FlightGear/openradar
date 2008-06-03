package de.knewcleus.openradar.radio;

import java.util.HashMap;
import java.util.Map;

public class MessageType {
	protected final String name;
	protected Map<String, Object> properties=new HashMap<String, Object>();
	
	public MessageType(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean hasProperty(String name) {
		return properties.containsKey(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getProperty(String name) {
		return (T)properties.get(name);
	}
	
	public void setProperty(String name, Object value) {
		properties.put(name,value);
	}
}
