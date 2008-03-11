package de.knewcleus.radar.aircraft;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.radar.radio.IEndpoint;

public interface IAircraft extends IUpdateable, IEndpoint {
	public enum FlightType {
		GA,Commercial,Military;
	}
	
	/**
	 * Determine the position of the radar target in geodetic longitude/latitude.
	 */
	public abstract Position getPosition();
	
	/**
	 * Determine pressure altitude.
	 */
	public abstract double getPressureAltitude();
	
	/**
	 * Determine the ground speed of the radar target.
	 */
	public abstract double getGroundSpeed();
	
	/**
	 * Determine the true course of the radar target.
	 */
	public abstract double getTrueCourse();
	
	public abstract FlightType getFlightType();

	public abstract String getOperator();
	
	public abstract String getCallsign();

	public abstract AircraftType getType();
}