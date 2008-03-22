package de.knewcleus.radar.aircraft;

import java.util.HashMap;
import java.util.Map;

public class CorrelationDatabase implements ICorrelationDatabase {
	protected final Map<String, String> correlationMap=new HashMap<String, String>();
	
	@Override
	public String correlateToCallsign(String squawk) {
		return correlationMap.get(squawk);
	}
	
	@Override
	public void registerSquawk(String squawk, String callsign) {
		correlationMap.put(squawk,callsign);
	}
	
	@Override
	public void unregisterSquawk(String squawk) {
		correlationMap.remove(squawk);
	}
}
