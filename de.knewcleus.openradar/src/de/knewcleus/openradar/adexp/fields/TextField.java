package de.knewcleus.openradar.adexp.fields;

public class TextField extends AbstractBasicField {
	protected final String value;

	public TextField(TextFieldDescriptor descriptor, String value) {
		super(descriptor);
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}
	
	@Override
	public String valueToString() {
		return value;
	}
}
