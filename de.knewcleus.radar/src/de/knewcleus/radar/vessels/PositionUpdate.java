package de.knewcleus.radar.vessels;

public class PositionUpdate {
	protected final Object trackIdentifier;
	protected final double longitude;
	protected final double latitude;
	protected final double groundSpeed;
	protected final double trueCourse;
	
	protected final SSRMode ssrMode;
	protected final String ssrCode;
	protected final double pressureAltitude;
	
	public PositionUpdate(Object trackIdentifier,
			double longitude, double latitude,
			double groundSpeed, double trueCourse,
			SSRMode ssrMode,
			String ssrCode,
			double pressureAltitude)
	{
		this.trackIdentifier=trackIdentifier;
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
