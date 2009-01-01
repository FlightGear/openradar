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
