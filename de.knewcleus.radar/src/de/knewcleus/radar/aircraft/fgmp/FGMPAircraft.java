package de.knewcleus.radar.aircraft.fgmp;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Quaternion;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.Player;
import de.knewcleus.fgfs.multiplayer.PlayerAddress;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;
import de.knewcleus.radar.aircraft.IRadarTarget;

public class FGMPAircraft extends Player implements IRadarTarget {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
	
	protected Position geodeticPosition=new Position();
	protected double groundSpeed=0f;
	protected double trueCourse=0f;
	
	public FGMPAircraft(PlayerAddress address, String callsign) {
		super(address, callsign);
	}
	
	@Override
	public Position getPosition() {
		return geodeticPosition;
	}
	
	@Override
	public boolean hasPressureAltitude() {
		return true;
	}
	
	@Override
	public double getPressureAltitude() {
		// FIXME: This is the actual altitude, not the pressure altitude
		return geodeticPosition.getZ();
	}
	
	@Override
	public double getGroundSpeed() {
		return groundSpeed;
	}
	
	@Override
	public double getTrueCourse() {
		return trueCourse;
	}
	
	@Override
	public String getSSRCode() {
		// FIXME: assign a temporary SSR code
		return getCallsign();
	}
	
	@Override
	public boolean hasSSRCode() {
		return true;
	}
	
	@Override
	public void updatePosition(long t, PositionMessage packet) {
		super.updatePosition(t, packet);
		geodeticPosition=geodToCartTransformation.backward(getCartesianPosition());
		
		groundSpeed=getLinearVelocity().getLength();
		
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
