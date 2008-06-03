package de.knewcleus.openradar.vessels.fgmp;

import de.knewcleus.fgfs.Units;
import de.knewcleus.fgfs.location.Ellipsoid;
import de.knewcleus.fgfs.location.GeodToCartTransformation;
import de.knewcleus.fgfs.location.Position;
import de.knewcleus.fgfs.location.Quaternion;
import de.knewcleus.fgfs.location.Vector3D;
import de.knewcleus.fgfs.multiplayer.Player;
import de.knewcleus.fgfs.multiplayer.protocol.PositionMessage;
import de.knewcleus.openradar.vessels.SSRMode;

public class FGMPAircraft extends Player {
	private static final GeodToCartTransformation geodToCartTransformation=new GeodToCartTransformation(Ellipsoid.WGS84);
	
	protected Position geodeticPosition=new Position();
	protected String simulatedSquawk;
	protected double groundSpeed=0f;
	protected double trueCourse=0f;
	
	public FGMPAircraft(String callsign, String simulatedSquawk) {
		super(callsign);
		this.simulatedSquawk=simulatedSquawk;
	}

	public double getLongitude() {
		return geodeticPosition.getX()*Units.DEG;
	}
	
	public double getLatitude() {
		return geodeticPosition.getY()*Units.DEG;
	}
	
	public double getGroundSpeed() {
		return groundSpeed;
	}
	
	public double getTrueCourse() {
		return trueCourse;
	}
	
	public SSRMode getSSRMode() {
		return SSRMode.MODEC;
	}
	
	public String getSSRCode() {
		return simulatedSquawk;
	}
	
	public double getPressureAltitude() {
		// FIXME: This is the actual altitude, not the pressure altitude
		return geodeticPosition.getZ()*Units.M;
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
