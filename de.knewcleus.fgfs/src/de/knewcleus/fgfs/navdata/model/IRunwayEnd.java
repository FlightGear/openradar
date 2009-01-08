package de.knewcleus.fgfs.navdata.model;

public interface IRunwayEnd extends INavPoint {
	public IRunway getRunway();
	public String getRunwayID();
	public float getTrueHeading();
	public float getStopwayLength();
	public float getDisplacedThreshold();
	public float getTORA();
	public float getLDA();
	public IRunwayEnd getOppositeEnd();
}
