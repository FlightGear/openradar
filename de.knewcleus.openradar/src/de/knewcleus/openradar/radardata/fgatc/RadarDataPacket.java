package de.knewcleus.openradar.radardata.fgatc;

import java.awt.geom.Point2D;

import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.ISSRData;

public class RadarDataPacket implements IRadarDataPacket {
	protected final Object trackingIdentifier;
	protected final PositionPacket packet;
	protected final Point2D position;
	protected final SSRData ssrData;
	protected final float groundspeed, trueCourse;

	public RadarDataPacket(TargetStatus targetStatus) {
		trackingIdentifier = targetStatus.getTargetIdentifier();
		packet = targetStatus.getLastPacket();
		position = new Point2D.Double( packet.getLongitude(), packet.getLatitude() );
		ssrData = new SSRData(packet);
		trueCourse = targetStatus.getTrueCourse();
		groundspeed = targetStatus.getGroundspeed();
	}

	@Override
	public float getTimestamp() {
		return packet.getPositionTime();
	}

	@Override
	public Object getTrackingIdentifier() {
		return trackingIdentifier;
	}

	@Override
	public boolean wasSeenOnLastScan() {
		/* We only send message of targets we have seen */
		return true;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

	@Override
	public ISSRData getSSRData() {
		if (!packet.isSSRActive() && !packet.isEncoderActive()) {
			return null;
		}
		return ssrData;
	}

	@Override
	public float getCalculatedTrueCourse() {
		return trueCourse;
	}

	@Override
	public float getCalculatedVelocity() {
		return groundspeed;
	}

}
