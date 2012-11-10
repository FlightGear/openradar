package de.knewcleus.fgfs.navdata.model;

import de.knewcleus.fgfs.navdata.impl.Glideslope;

public interface IRunwayEnd extends INavPoint {
	public IRunway getRunway();
	public String getRunwayID();
	public float getTrueHeading();
	public float getStopwayLength();
	public float getDisplacedThreshold();
	public float getTORA();
	public float getLDA();
	public IRunwayEnd getOppositeEnd();
    public void setGlideslope(Glideslope gs);
    public boolean isActive();
    public boolean isLandingActive();
    public boolean isStartingActive();
    public Glideslope getGlideslope();
}
