package de.knewcleus.openradar.sector.impl;

import de.knewcleus.openradar.sector.GeographicPosition;

public class GeographicPositionImpl implements GeographicPosition {
	protected double latitude = 0.0;
	protected double longitude = 0.0;

	@Override
	public double getLatitude() {
		return latitude;
	}

	@Override
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}
