package de.knewcleus.fgfs.navdata;

import de.knewcleus.fgfs.navdata.model.INavDataStream;
import de.knewcleus.fgfs.navdata.model.INavDatum;

public class FilteredNavDataStream<T extends INavDatum> implements INavDataStream<T> {
	protected final INavDataStream<T> stream;
	protected final INavDatumFilter<? super T> filter;
	
	public FilteredNavDataStream(INavDataStream<T> stream, INavDatumFilter<? super T> filter) {
		this.stream = stream;
		this.filter = filter;
	}
	
	@Override
	public T readDatum() throws NavDataStreamException {
		T datum;
		do {
			datum = stream.readDatum();
		} while (datum!=null && !filter.allow(datum));
		return datum;
	}
}
