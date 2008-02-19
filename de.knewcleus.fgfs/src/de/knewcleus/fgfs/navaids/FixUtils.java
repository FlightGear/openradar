package de.knewcleus.fgfs.navaids;

import de.knewcleus.fgfs.location.Position;

public class FixUtils {
	/**
	 * Determine the true bearing from station to current position (QTE).
	 */
	public static float getQTE(Position current, Position station) {
		double dx=station.getX()-current.getX();
		double dy=station.getY()-current.getY();
		
		float qte=(float)Math.toDegrees(Math.atan2(dy, dx));
		
		if (qte<=0.0)
			qte+=360.0;
		if (qte>360.0)
			qte-=360.0;
		
		return qte;
	}
	
	/**
	 * Determine the true bearing from current position to station (QUJ).
	 */
	public static float getQUJ(Position current, Position station) {
		return getQTE(station,current);
	}
	
	/**
	 * Determine the magnetic bearing from current position to station (QDM).
	 */
	public static float getQDM(Position current, Position station) {
		return getQDR(station,current);
	}
	
	/**
	 * Determine the magnetic bearing from station to current position (QDR/radial).
	 */
	public static float getQDR(Position current, Position station) {
		float qte=getQTE(current,station);
		
		float qdr=qte;
		// TODO: correct for variation
		
		return qdr;
	}
	
	/**
	 * Determine the lateral position relative to the given line of position (LOP)
	 * of the given station.
	 * 
	 * The line of position is oriented in the given direction. The lateral position is positive when
	 * current position is left of the LOP as seen when looking in the direction given.
	 */
	public static double getRelativePosition(Position current, Position station, float radial) {
		double vrx,vry;
		
		vrx=Math.sin(Math.toRadians(radial));
		vry=Math.cos(Math.toRadians(radial));
		
		double dx,dy;
		dx=current.getX()-station.getX();
		dy=current.getY()-station.getY();
		
		return -vry*dx+vrx*dy;
	}
	
	/**
	 * Determine the distance from the given station.
	 */
	public static double getDistance(Position current, Position station) {
		double dx,dy;
		dx=current.getX()-station.getX();
		dy=current.getY()-station.getY();
		
		return Math.sqrt(dx*dx+dy*dy);
	}
}
