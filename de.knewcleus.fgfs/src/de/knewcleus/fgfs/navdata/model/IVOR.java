package de.knewcleus.fgfs.navdata.model;

public interface IVOR extends INavPointWithElevation, INavDatumWithID,
		INamedNavDatum, ITransmitter
{
	public float getVariation();
}
