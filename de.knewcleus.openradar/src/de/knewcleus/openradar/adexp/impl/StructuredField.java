package de.knewcleus.openradar.adexp.impl;

import de.knewcleus.openradar.adexp.IStructuredField;

public class StructuredField extends AbstractFieldContainer implements IStructuredField {
	protected final String fieldName;
	
	public StructuredField(String fieldName) {
		this.fieldName = fieldName;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String toString() {
		return "-"+getFieldName()+" "+super.toString();
	}
}
