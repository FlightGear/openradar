package de.knewcleus.fgfs.util;

/**
 * An output iterator receives elements from some traversal entity.
 * 
 * @author Ralf Gerlich
 *
 * @param <T>	The type of elements to be traversed.
 */
public interface IOutputIterator<T> {
	public boolean wantsNext();
	
	public void next(T v);

}
