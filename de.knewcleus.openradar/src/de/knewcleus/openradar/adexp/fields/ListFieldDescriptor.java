package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IListFieldDescriptor;

public class ListFieldDescriptor extends TypedFieldContainerDescriptor implements IListFieldDescriptor {
	protected final String fieldName;

	public ListFieldDescriptor(String fieldName) {
		this.fieldName = fieldName;
	}

	public ListFieldDescriptor(String fieldName, FieldContainerType type) {
		super(type);
		this.fieldName = fieldName;
	}
	
	@Override
	public String getFieldName() {
		return fieldName;
	}
}
