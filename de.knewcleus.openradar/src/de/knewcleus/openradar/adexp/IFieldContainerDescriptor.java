package de.knewcleus.openradar.adexp;

public interface IFieldContainerDescriptor extends Iterable<IFieldDescriptor>
{
	public boolean hasField(String name);
	public IFieldDescriptor getFieldDescriptor(String name);
}
