/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * 
 * This file is part of OpenRadar.
 * 
 * OpenRadar is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * OpenRadar is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * OpenRadar. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Diese Datei ist Teil von OpenRadar.
 * 
 * OpenRadar ist Freie Software: Sie können es unter den Bedingungen der GNU
 * General Public License, wie von der Free Software Foundation, Version 3 der
 * Lizenz oder (nach Ihrer Option) jeder späteren veröffentlichten Version,
 * weiterverbreiten und/oder modifizieren.
 * 
 * OpenRadar wird in der Hoffnung, dass es nützlich sein wird, aber OHNE JEDE
 * GEWÄHELEISTUNG, bereitgestellt; sogar ohne die implizite Gewährleistung der
 * MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU General
 * Public License für weitere Details.
 * 
 * Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
 * Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
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