package de.knewcleus.fgfs.navdata;

import de.knewcleus.fgfs.navdata.model.INavDatum;

public interface INavDatumFilter<T extends INavDatum> {
	public boolean allow(T datum);
}
