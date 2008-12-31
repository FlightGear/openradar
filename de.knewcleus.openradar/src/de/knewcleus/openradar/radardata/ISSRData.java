package de.knewcleus.openradar.radardata;

/**
 * Secondary Surveilance Radar (SSR) data provided by a radar station.
 * 
 * @author Ralf Gerlich
 *
 */
public interface ISSRData {
	/**
	 * @return true, iff Standard Mark X Mode A code is available.
	 */
	public boolean hasMarkXModeACode();
	
	/**
	 * @return Standard Mark X Mode A code, if any, or null, if none available
	 */
	public String getMarkXModeACode();
	
	/**
	 * @return true, iff Standard Mark X Mode C elevation is available.
	 */
	public boolean hasMarkXModeCElevation();
	
	/**
	 * @return Standard Mark X Mode C elevation in feet, if any
	 */
	public float getMarkXModeCElevation();
	
	/**
	 * @return true, iff Standard Mark X Special Purpose Indicator was received.
	 */
	public boolean hasMarkXSPI();
}
