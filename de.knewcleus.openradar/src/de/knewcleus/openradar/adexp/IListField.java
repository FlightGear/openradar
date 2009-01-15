package de.knewcleus.openradar.adexp;

public interface IListField extends IField, Iterable<IField> {
	public int size();
	public IField get(int index) throws IndexOutOfBoundsException;
	@Override
	public IListFieldDescriptor getDescriptor();
}
