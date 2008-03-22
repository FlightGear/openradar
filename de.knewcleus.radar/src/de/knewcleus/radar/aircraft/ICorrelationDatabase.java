package de.knewcleus.radar.aircraft;

public interface ICorrelationDatabase {
	public String correlateToCallsign(String squawk);
	public void registerSquawk(String squawk, String callsign);
	public void unregisterSquawk(String squawk);
}
