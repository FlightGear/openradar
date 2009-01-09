package de.knewcleus.fgfs.navdata;

import java.util.LinkedList;
import java.util.List;

import de.knewcleus.fgfs.navdata.model.INavDatum;

public class NavDatumFilterChain<T extends INavDatum> implements INavDatumFilter<T> {
	public enum Kind {
		CONJUNCT, DISJUNCT
	};
	protected final Kind kind;
	protected final List<INavDatumFilter<T>> filterList = new LinkedList<INavDatumFilter<T>>();

	public NavDatumFilterChain(Kind kind) {
		this.kind = kind;
	}
	
	public void add(INavDatumFilter<T> filter) {
		filterList.add(filter);
	}
	
	@Override
	public boolean allow(T datum) {
		final boolean abortValue = (kind==Kind.DISJUNCT);
		for (INavDatumFilter<T> filter: filterList) {
			if (filter.allow(datum)==abortValue) {
				return abortValue;
			}
		}
		return !abortValue;
	}
}
