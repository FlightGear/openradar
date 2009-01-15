package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IListFieldDescriptor;

public class ListFieldDescriptor extends AbstractFieldContainerDescriptor implements IListFieldDescriptor {
	protected final String fieldName;

	public ListFieldDescriptor(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}
}
