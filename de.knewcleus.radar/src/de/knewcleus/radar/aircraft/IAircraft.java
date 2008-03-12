package de.knewcleus.radar.aircraft;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.radar.radio.IEndpoint;

public interface IAircraft extends IUpdateable, IEndpoint, IRadarTarget {
	public enum FlightType {
		GA,Commercial,Military;
	}
	
	public abstract FlightType getFlightType();

	public abstract String getCallsign();

	public abstract AircraftType getType();

	public abstract String getOperator();
}