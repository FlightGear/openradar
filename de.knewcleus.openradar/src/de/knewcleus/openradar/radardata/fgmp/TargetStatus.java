package de.knewcleus.openradar.radardata.fgmp;

import java.math.BigDecimal;

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
	
	protected volatile Position geodeticPosition=new Position();
    private volatile Position lastPosition = new Position();
    private volatile Vector3D linearVelocityHF = new Vector3D();
    private volatile String frequency;
    
	protected volatile double groundSpeed=0f;
	protected volatile double trueCourse=0f;
    protected volatile double heading=0f;

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
	
	public Position getLastPostion() {
	    return lastPosition;
	}
	
	@Override
	public void updatePosition(long t, PositionMessage packet) {
		super.updatePosition(t, packet);
		lastPosition = geodeticPosition; 
		geodeticPosition=geodToCartTransformation.backward(getCartesianPosition());
		
		groundSpeed=getLinearVelocity().getLength()*Units.MPS;
		
		final Quaternion hf2gcf=Quaternion.fromLatLon(geodeticPosition.getY(), geodeticPosition.getX());
		final Quaternion gcf2hf=hf2gcf.inverse();
		final Quaternion bf2hf=gcf2hf.multiply(orientation);
		linearVelocityHF=bf2hf.transform(getLinearVelocity());
		
		trueCourse=Math.atan2(linearVelocityHF.getY(), linearVelocityHF.getX())*Units.RAD;
		
		if (trueCourse<=0*Units.DEG) {
			trueCourse+=360*Units.DEG;
		} else if (trueCourse>360*Units.DEG) {
			trueCourse-=360*Units.DEG;
		}
		
        heading = bf2hf.getAngle();
		
		String freq = (String)packet.getProperty("sim/multiplay/transmission-freq-hz");
		if(freq!=null) {
    		BigDecimal bdFreq = new BigDecimal(freq);
            bdFreq = bdFreq.divide(new BigDecimal(1000000));
            frequency = String.format("%1.1f", bdFreq);
		}
		// model may change too if you exit and return with same callsign
		this.model = packet.getModel();
	}
	
	public String getFrequency() {
	    return frequency;
	}
	/**
	 * Returns he velocity vector aligned with the global coordinates
	 * 
	 * @return
	 */
	public Vector3D getLinearVelocityGlobal() {
	    return linearVelocityHF;
	}

    public double getHeading() {
        return heading;
    }
    
    public String toString() { return callsign; }
}
