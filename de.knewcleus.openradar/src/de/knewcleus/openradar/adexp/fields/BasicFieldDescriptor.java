package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IBasicFieldDescriptor;

public abstract class BasicFieldDescriptor implements IBasicFieldDescriptor {
	protected final String fieldName;
	
	public BasicFieldDescriptor(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

}
