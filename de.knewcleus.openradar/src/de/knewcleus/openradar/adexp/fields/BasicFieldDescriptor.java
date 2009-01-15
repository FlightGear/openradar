package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IBasicFieldDescriptor;
import de.knewcleus.openradar.adexp.IFieldParser;

public class BasicFieldDescriptor implements IBasicFieldDescriptor {
	protected final String fieldName;
	protected final IFieldParser fieldParser;
	
	public BasicFieldDescriptor(String fieldName, IFieldParser fieldParser) {
		this.fieldName = fieldName;
		this.fieldParser = fieldParser;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public IFieldParser getFieldParser() {
		return fieldParser;
	}

}
