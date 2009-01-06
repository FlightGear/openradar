package de.knewcleus.fgfs.navdata;

public interface INavDatumFilter<T extends INavDatum> {
	public boolean allow(T datum);
}
