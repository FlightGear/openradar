package de.knewcleus.radar.aircraft;

import de.knewcleus.fgfs.location.Position;

public interface IRadarTarget {
	/**
	 * Determine the position of the radar target in geodetic longitude/latitude.
	 */
	public abstract Position getPosition();
	
	/**
	 * Determine whether the target provides pressure altitude
	 */
	public abstract boolean hasPressureAltitude();

	/**
	 * Determine pressure altitude.
	 */
	public abstract double getPressureAltitude();
	
	/**
	 * Determine the ground speed of the radar target.
	 */
	public abstract double getGroundSpeed();

	/**
	 * Determine the true course of the radar target.
	 */
	public abstract double getTrueCourse();
	
	/**
	 * Determine whether the target provides an SSR code
	 */
	public abstract boolean hasSSRCode();
	
	/**
	 * Determine the SSR code of the target, if any.
	 */
	public abstract String getSSRCode();
}