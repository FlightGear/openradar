package de.knewcleus.openradar.adexp.fields;

public class ADEXPMessageType extends FieldContainerType {

	public ADEXPMessageType() {
		addField(new TextFieldDescriptor("TITLE"));
	}

}
