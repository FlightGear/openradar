package de.knewcleus.openradar.adexp.fields;

public class TextField extends AbstractBasicField {
	protected final String value;

	public TextField(String fieldName, String value) {
		super(fieldName);
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	
	@Override
	public String toString() {
		return "-"+getFieldName()+" "+value;
	}
}
