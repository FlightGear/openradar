package de.knewcleus.openradar.adexp;

/**
 * A list field is a compound field in which the order of fields is preserved.
 *  
 * @author Ralf Gerlich
 *
 */
public interface IListField extends ICompoundField {
	/**
	 * Fetch the field at the given index.
	 * @throws IndexOutOfBoundsException
	 * 			if <code>index&lt;0</code> or <code>index>=size()</code> 
	 */
	public IField get(int index) throws IndexOutOfBoundsException;
	
	@Override
	public IListFieldDescriptor getDescriptor();
}
