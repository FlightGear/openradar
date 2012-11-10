package de.knewcleus.openradar.radardata.fgmp;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Quaternion;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.Player;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;
import de.knewcleus.openradar.radardata.IRadarDataPacket;

public class TargetStatus extends Player {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
	
	protected Position geodeticPosition=new Position();
	protected double groundSpeed=0f;
	protected double trueCourse=0f;

	public TargetStatus(String callsign) {
		super(callsign);
	}
	
	public IRadarDataPacket getRadarDataPacket() {
		return new RadarDataPacket(this);
	}
	
	public Position getGeodeticPosition() {
		return geodeticPosition;
	}
	
	public double getGroundSpeed() {
		return groundSpeed;
	}
	
	public double getTrueCourse() {
		return trueCourse;
	}
	
	@Override
	public void updatePosition(long t, PositionMessage packet) {
		super.updatePosition(t, packet);
		geodeticPosition=geodToCartTransformation.backward(getCartesianPosition());
		
		groundSpeed=getLinearVelocity().getLength()*Units.MPS;
		
		final Quaternion hf2gcf=Quaternion.fromLatLon(geodeticPosition.getY(), geodeticPosition.getX());
		final Quaternion gcf2hf=hf2gcf.inverse();
		final Quaternion bf2hf=gcf2hf.multiply(orientation);
		final Vector3D linearVelocityHF=bf2hf.transform(getLinearVelocity());
		
		trueCourse=Math.atan2(linearVelocityHF.getY(), linearVelocityHF.getX())*Units.RAD;
		
		if (trueCourse<=0*Units.DEG) {
			trueCourse+=360*Units.DEG;
		} else if (trueCourse>360*Units.DEG) {
			trueCourse-=360*Units.DEG;
		}
	}
}
