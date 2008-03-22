package de.knewcleus.radar.aircraft;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CorrelationDatabase implements ICorrelationDatabase {
	protected final static Logger logger=Logger.getLogger(CorrelationDatabase.class.getName());
	protected final Map<String, String> correlationMap=new HashMap<String, String>();
	
	@Override
	public synchronized String correlateToCallsign(String squawk) {
		return correlationMap.get(squawk);
	}
	
	@Override
	public synchronized void registerSquawk(String squawk, String callsign) {
		logger.info("Registering squawk "+squawk+" to callsign "+callsign);
		if (correlationMap.containsKey(squawk)) {
			logger.warning("Squawk "+squawk+" already registered to "+correlationMap.get(squawk));
		}
		correlationMap.put(squawk,callsign);
	}
	
	@Override
	public synchronized void unregisterSquawk(String squawk) {
		logger.info("Unregistering squawk "+squawk);
		correlationMap.remove(squawk);
	}
}
