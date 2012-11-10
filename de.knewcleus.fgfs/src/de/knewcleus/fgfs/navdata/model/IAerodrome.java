package de.knewcleus.fgfs.navdata.model;

import java.util.List;

import de.knewcleus.fgfs.navdata.xplane.RawFrequency;

public interface IAerodrome extends INavPointWithElevation, INamedNavDatum, INavDatumWithID {
	public enum Type {
		Land, Sea, Heliport;
	}
	
	public Type getType();

	public List<RawFrequency> getFrequencies();
	
    public void setFrequencies(List<RawFrequency> frequencies);
}
