package de.knewcleus.openradar.radardata;

import java.awt.geom.Point2D;


/**
 * Radar packets contain data about a target detected by surveillance radar.
 * 
 * It may include secondary radar data.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IRadarDataPacket {
	/**
	 * Determine whether the target was actually seen in the last
	 * radar antenna scan.
	 * 
	 * If the target was not seen, the data from the last available
	 * scan or estimated data is provided.
	 * 
	 * @return <code>true</code>, if and only if the target was seen
	 *         in the last scan.
	 */
	public abstract boolean wasSeenOnLastScan();

	/**
	 * Every radar datum is associated with a timestamp expressed in seconds.
	 */
	public abstract float getTimestamp();

	/**
	 * @return the geographic position of the radar target.
	 */
	public abstract Point2D getPosition();

	/**
	 * @return the tracking identifier.
	 */
	public abstract Object getTrackingIdentifier();

	/**
	 * @return SSR data, if any, or null if no SSR data is available for this target.
	 */
	public abstract ISSRData getSSRData();

	/**
	 * @return the calculated velocity.
	 */
	public float getCalculatedVelocity();
	
	/**
	 * @return the calculated true course.
	 */
	public float getCalculatedTrueCourse();
}
