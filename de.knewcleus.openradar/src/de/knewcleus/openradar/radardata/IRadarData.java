package de.knewcleus.openradar.radardata;

import java.awt.geom.Point2D;

/**
 * Radar data contains data about a target detected by surveillance radar.
 * 
 * It may include secondary radar data.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IRadarData {
	/**
	 * @return the geographic position of the radar target.
	 */
	public Point2D getPosition();
	
	/**
	 * @return the tracking identifier.
	 */
	public Object getTrackingIdentifier();
	
	/**
	 * @return the calculated velocity.
	 */
	public float getCalculatedVelocity();
	
	/**
	 * @return the calculated track direction.
	 */
	public float getCalculatedTrackDirection();
	
	/**
	 * @return SSR data, if any, or null if no SSR data is available for this target.
	 */
	public ISSRData getSSRData();
}
