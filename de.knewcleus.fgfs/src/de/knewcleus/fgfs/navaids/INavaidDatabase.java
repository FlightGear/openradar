package de.knewcleus.fgfs.navaids;


public interface INavaidDatabase {

	public abstract NamedFixDB getFixDB();

	public abstract AirwayDB getAirwayDB();

}