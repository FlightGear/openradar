package de.knewcleus.openradar.textview;

import de.knewcleus.fgfs.navdata.INavDatumFilter;
import de.knewcleus.fgfs.navdata.model.INavDatum;

public class TypeFilter<T extends INavDatum> implements INavDatumFilter<T> {
	protected final Class<? extends T> allowedClass;
	
	public TypeFilter(Class<? extends T> allowedClass) {
		this.allowedClass = allowedClass;
	}
	
	@Override
	public boolean allow(INavDatum datum) {
		return allowedClass.isInstance(datum);
	}

}
