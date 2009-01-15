package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IBasicFieldDescriptor;

public class TextField extends AbstractBasicField {
	protected final String value;

	public TextField(IBasicFieldDescriptor descriptor, String value) {
		super(descriptor);
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	
	@Override
	public String toString() {
		return "-"+descriptor.getFieldName()+" "+value;
	}
}
