package de.knewcleus.fgfs.navdata.model;

import de.knewcleus.fgfs.navdata.NavDataStreamException;

/**
 * An <code>INavDataStream</code> provides a stream of {@link INavDatum} elements.
 * 
 * @author Ralf Gerlich
 *
 * @param <T>	The type of navigation datum delivered.
 */
public interface INavDataStream<T extends INavDatum> {
	/**
	 * Read the next datum from the stream.
	 * 
	 * @return the next datum or <code>null</code> if no further datum is available
	 * @throws NavDataStreamException
	 */
	public T readDatum() throws NavDataStreamException;
}
