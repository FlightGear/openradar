package de.knewcleus.fgfs.navdata.model;

public interface IVOR extends IIntersection, INavPointWithElevation,
		INamedNavDatum, ITransmitter
{
	public float getVariation();
}
