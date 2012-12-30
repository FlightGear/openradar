package de.knewcleus.fgfs.navdata.model;

import java.awt.geom.Point2D;
import java.util.List;

import de.knewcleus.fgfs.navdata.xplane.RawFrequency;

public interface IAerodrome extends INavPointWithElevation, INamedNavDatum, INavDatumWithID {
	public enum Type {
		Land, Sea, Heliport;
	}
	public Point2D getTowerPosition();
	
	public Type getType();

	public List<RawFrequency> getFrequencies();
	
    public void setFrequencies(List<RawFrequency> frequencies);
}
