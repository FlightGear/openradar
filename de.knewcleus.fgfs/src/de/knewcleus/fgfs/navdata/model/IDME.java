package de.knewcleus.fgfs.navdata.model;

public interface IDME extends IIntersection, INavPointWithElevation,
	INamedNavDatum, ITransmitter
{
	public float getDistanceBias();
}
