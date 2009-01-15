package de.knewcleus.openradar.adexp;

public interface IStructuredField extends IField, IFieldContainer {
	@Override
	public IStructuredFieldDescriptor getDescriptor();
}
