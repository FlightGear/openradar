package de.knewcleus.fgfs.navdata.model;

public interface ILandingSurface {

    public IAerodrome getAerodrome();
    
    public abstract String getAirportID();

	public abstract SurfaceType getSurfaceType();

	public abstract float getLength();

	public abstract float getWidth();

}