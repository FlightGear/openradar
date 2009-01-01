/**
 * 
 */
package de.knewcleus.openradar.radardata.fgatc;

public class PositionPacket {
	protected final float positionTime;
	protected final double longitude;
	protected final double latitude;
	
	protected final boolean ssrActive;
	protected final String ssrCode;
	
	protected final boolean encoderActive;
	protected final float encoderAltitude;
	
	protected final boolean specialPurposeIndicator;
	
	public PositionPacket(float positionTime, double longitude, double latitude,
			boolean ssrActive, String ssrCode, boolean encoderActive,
			float encoderAltitude,
			boolean specialPurposeIndicator) {
		this.positionTime = positionTime;
		this.longitude = longitude;
		this.latitude = latitude;
		this.ssrActive = ssrActive;
		this.ssrCode = ssrCode;
		this.encoderActive = encoderActive;
		this.encoderAltitude = encoderAltitude;
		this.specialPurposeIndicator = specialPurposeIndicator;
	}

	public float getPositionTime() {
		return positionTime;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public boolean isSSRActive() {
		return ssrActive;
	}

	public String getSSRCode() {
		return ssrCode;
	}

	public boolean isEncoderActive() {
		return encoderActive;
	}

	public float getEncoderAltitude() {
		return encoderAltitude;
	}

	public boolean hasSpecialPurposeIndicator() {
		return specialPurposeIndicator;
	}
}