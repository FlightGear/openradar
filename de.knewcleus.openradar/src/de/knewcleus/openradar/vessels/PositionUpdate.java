package de.knewcleus.openradar.vessels;

/**
 * A position update represents information about a newly detected position of a given target.
 * 
 * It is associated with a track identifier, a timestamp, the position, speed and true course of
 * the target, and, if available, SSR data including the SSR mode and code in use as well as the
 * pressure altitude transmitted, if available.
 * 
 * The timestamp is attached by the position data provider and represents the actual time of detection
 * for the presented position of the given target. Each target may have its own time reference, so
 * no synchronization between timestamps of different targets may be assumed.
 * 
 * PositionUpdate instances are non-modifiable.
 * 
 * @author Ralf Gerlich
 *
 */
public class PositionUpdate {
	protected final Object trackIdentifier;
	protected final double timestamp;
	protected final double longitude;
	protected final double latitude;
	protected final double groundSpeed;
	protected final double trueCourse;
	
	protected final SSRMode ssrMode;
	protected final String ssrCode;
	protected final double pressureAltitude;
	
	public PositionUpdate(Object trackIdentifier,
			double timestamp,
			double longitude, double latitude,
			double groundSpeed, double trueCourse,
			SSRMode ssrMode,
			String ssrCode,
			double pressureAltitude)
	{
		this.trackIdentifier=trackIdentifier;
		this.timestamp=timestamp;
		this.longitude=longitude;
		this.latitude=latitude;
		this.groundSpeed=groundSpeed;
		this.trueCourse=trueCourse;
		this.ssrMode=ssrMode;
		this.ssrCode=ssrCode;
		this.pressureAltitude=pressureAltitude;
	}

	public Object getTrackIdentifier() {
		return trackIdentifier;
	}
	
	public double getTimestamp() {
		return timestamp;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getGroundSpeed() {
		return groundSpeed;
	}

	public double getTrueCourse() {
		return trueCourse;
	}

	public SSRMode getSSRMode() {
		return ssrMode;
	}

	public String getSSRCode() {
		return ssrCode;
	}

	public double getPressureAltitude() {
		return pressureAltitude;
	}
	
	@Override
	public String toString() {
		return "(track "+trackIdentifier+" lon="+longitude+" lat="+latitude+" gs="+groundSpeed+" tc="+trueCourse+" ssrMode="+ssrMode+" ssrCode="+ssrCode+" pressureAlt="+pressureAltitude+")";
	}
}
