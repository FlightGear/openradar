package de.knewcleus.openradar.adexp;

public interface IField {
	@Deprecated
	public String getFieldName();
	public IFieldDescriptor getDescriptor();
}
