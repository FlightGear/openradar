package de.knewcleus.openradar.adexp;

import java.util.Iterator;

/**
 * A field container represents an entity which contains fields.
 * 
 * Fields are addressed by name and there may be more than one field
 * with a given name.
 * 
 * @author Ralf Gerlich
 *
 */
public interface IFieldContainer extends Iterable<IField> {
	/**
	 * @return the number of individual fields in this container.
	 */
	public int size();
	
	/**
	 * @return <code>true</code>, if and only if the container contains a field
	 *         with the given name.
	 */
	public boolean hasField(String fieldname);
	
	/**
	 * @return the number of occurrences of the given field name.
	 */
	public int countByName(String fieldname);
	
	/**
	 * @return an iterator for all fields of the given name.
	 */
	public Iterator<IField> fieldIterator(String fieldname);
	
	/**
	 * @return the descriptor for this field container.
	 */
	public IFieldContainerDescriptor getDescriptor();
}
