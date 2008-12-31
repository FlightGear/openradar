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
	 * Determine whether the target was actually seen in the last
	 * radar antenna scan.
	 * 
	 * If the target was not seen, the data from the last available
	 * scan or estimated data is provided.
	 * 
	 * @return <code>true</code>, if and only if the data in this record
	 *         is estimated.
	 */
	public boolean isEstimate();
	
	/**
	 * Every radar datum is associated with a timestamp.
	 */
	public float getTimestamp();
	
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
