package de.knewcleus.radar.aircraft;

import de.knewcleus.fgfs.IUpdateable;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.radar.radio.IEndpoint;

public interface IAircraft extends IUpdateable, IEndpoint {
	public enum FlightType {
		GA,Commercial,Military;
	}
	
	/**
	 * Determine the position of the radar target in geodetic longitude/latitude/altitude.
	 */
	public abstract Position getPosition();
	
	/**
	 * Determine the velocity vector of the radar target in geocentric cartesian coordinates.
	 */
	public abstract Vector3D getVelocityVector();
	
	public abstract FlightType getFlightType();

	public abstract String getOperator();
	
	public abstract String getCallsign();

	public abstract AircraftType getType();
}