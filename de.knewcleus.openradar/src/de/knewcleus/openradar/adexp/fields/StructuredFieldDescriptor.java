package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IStructuredFieldDescriptor;

public class StructuredFieldDescriptor extends TypedFieldContainerDescriptor
		implements IStructuredFieldDescriptor {
	protected final String fieldName;

	public StructuredFieldDescriptor(String fieldName) {
		this.fieldName = fieldName;
	}

	public StructuredFieldDescriptor(String fieldName, FieldContainerType type) {
		super(type);
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

}
