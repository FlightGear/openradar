package de.knewcleus.fgfs.navdata.model;

public interface IAerodrome extends INavPointWithElevation, INamedNavDatum, INavDatumWithID {
	public enum Type {
		Land, Sea, Heliport;
	}
	
	public Type getType();
	public String[] getRunwayIDs();
}
