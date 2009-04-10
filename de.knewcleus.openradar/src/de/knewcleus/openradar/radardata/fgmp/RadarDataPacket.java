package de.knewcleus.openradar.radardata.fgmp;

import java.awt.geom.Point2D;

import de.knewcleus.fgfs.location.Position;
import de.knewcleus.openradar.radardata.IRadarDataPacket;
import de.knewcleus.openradar.radardata.ISSRData;

public class RadarDataPacket implements IRadarDataPacket {
	protected final TargetStatus targetStatus;
	protected float timestamp;
	protected final Point2D position;
	protected final float trueCourse;
	protected final float groundSpeed;

	public RadarDataPacket(TargetStatus targetStatus) {
		this.targetStatus=targetStatus;
		this.timestamp=(float)targetStatus.getPositionTime();
		final Position geodeticPosition=targetStatus.getGeodeticPosition();
		position=new Point2D.Double(geodeticPosition.getX(), geodeticPosition.getY());
		groundSpeed=(float)targetStatus.getLinearVelocity().getLength();
		trueCourse=(float)targetStatus.getTrueCourse();
	}
	
	@Override
	public Object getTrackingIdentifier() {
		return targetStatus;
	}

	@Override
	public float getTimestamp() {
		return timestamp;
	}

	@Override
	public Point2D getPosition() {
		return position;
	}

	@Override
	public boolean wasSeenOnLastScan() {
		/* We only send packets on targets we have seen */
		return true;
	}

	@Override
	public ISSRData getSSRData() {
		/* FG Multiplayer does not transmit SSR data */
		return null;
	}

	@Override
	public float getCalculatedTrueCourse() {
		return trueCourse;
	}

	@Override
	public float getCalculatedVelocity() {
		return groundSpeed;
	}

}
