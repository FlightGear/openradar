package de.knewcleus.radar.aircraft;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.radio.IEndpoint;

public interface IAircraft extends IUpdateable, IEndpoint {
	public enum FlightType {
		GA,Commercial,Military;
	}
	
	public abstract FlightType getFlightType();

	public abstract String getOperator();
	
	public abstract String getCallsign();

	public abstract AircraftType getType();

	public abstract Position getPosition();

	public abstract double getVelocity();
}