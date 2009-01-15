package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IStructuredFieldDescriptor;

public class StructuredFieldDescriptor extends AbstractFieldContainerDescriptor
		implements IStructuredFieldDescriptor
{
	protected final String fieldName;

	public StructuredFieldDescriptor(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}
}
