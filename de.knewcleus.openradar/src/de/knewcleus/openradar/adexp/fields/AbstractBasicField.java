package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IBasicField;

public abstract class AbstractBasicField implements IBasicField {
	protected final String fieldName;

	public AbstractBasicField(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}
}
