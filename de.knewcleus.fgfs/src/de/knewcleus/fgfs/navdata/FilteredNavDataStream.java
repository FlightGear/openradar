package de.knewcleus.fgfs.navdata;

public class FilteredNavDataStream<T extends INavDatum> implements INavDataStream<T> {
	protected final INavDataStream<T> delegate;
	protected final INavDatumFilter<T> filter;
	
	public FilteredNavDataStream(INavDataStream<T> stream, INavDatumFilter<T> filter) {
		this.delegate = stream;
		this.filter = filter;
	}
	
	@Override
	public T readDatum() throws NavDataStreamException {
		T datum;
		do {
			datum = delegate.readDatum();
		} while (datum!=null && !filter.allow(datum));
		return datum;
	}
}
