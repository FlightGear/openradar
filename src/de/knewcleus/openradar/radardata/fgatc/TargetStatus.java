/**
 * Copyright (C) 2008-2009 Ralf Gerlich 
 * Copyright (C) 2012,2013 Wolfram Wagner
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

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodesicUtils;
import de.knewcleus.fgfs.location.GeodesicUtils.GeodesicInformation;

public class TargetStatus {
	protected static final GeodesicUtils geodesicUtils = new GeodesicUtils(Ellipsoid.WGS84);
	protected final Object targetIdentifier;
	
	protected long lastPacketTime;
	protected long lastAntennaRotationTime;
	protected PositionPacket lastPacket;
	protected float groundspeed = 0.0f;
	protected float trueCourse = 0.0f;
	
	public TargetStatus(Object targetIdentifier) {
		this.targetIdentifier = targetIdentifier;
	}
	
	public Object getTargetIdentifier() {
		return targetIdentifier;
	}

	public long getLastPacketTime() {
		return lastPacketTime;
	}
	
	public long getLastAntennaRotationTime() {
		return lastAntennaRotationTime;
	}
	
	public void setLastAntennaRotationTime(long lastAntennaRotationTime) {
		this.lastAntennaRotationTime = lastAntennaRotationTime;
	}
	
	public PositionPacket getLastPacket() {
		return lastPacket;
	}
	
	public float getGroundspeed() {
		return groundspeed;
	}
	
    public float getTrueCourse() {
		return trueCourse;
	}
	
	public void update(PositionPacket packet) {
		updateTrackVector(packet);
		lastPacket = packet;
		lastPacketTime = System.currentTimeMillis();
	}
	
	protected void updateTrackVector(PositionPacket packet) {
		if (lastPacket == null)
			return;
		final double t = (packet.getPositionTime() - lastPacket.getPositionTime()) * Units.SEC;
		final GeodesicInformation geodesicInformation = geodesicUtils.inverse(
				lastPacket.getLongitude(), lastPacket.getLatitude(),
				packet.getLongitude(), packet.getLatitude());
		
		trueCourse = (float)(geodesicInformation.getEndAzimuth() - 180.0 * Units.DEG);
		if (trueCourse < 0.0) {
			trueCourse += 360.0 * Units.DEG;
		}
		groundspeed = (float)(geodesicInformation.getLength() / t);
	}
}
