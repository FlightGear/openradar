package de.knewcleus.fgfs.navdata.model;

public interface IRunway extends ILandingSurface {

    public void setStartSide(IRunwayEnd rwe);

    public IRunwayEnd getStartSide();

    public void setLandSide(IRunwayEnd rwe);

    public IRunwayEnd getLandSide();
}
