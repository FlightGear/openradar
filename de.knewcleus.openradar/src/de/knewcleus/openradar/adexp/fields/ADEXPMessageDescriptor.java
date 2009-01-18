package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IADEXPMessageDescriptor;

public class ADEXPMessageDescriptor extends TypedFieldContainerDescriptor
		implements IADEXPMessageDescriptor {
	
	public ADEXPMessageDescriptor() {
		super(new ADEXPMessageType());
	}

	public ADEXPMessageDescriptor(FieldContainerType type) {
		super(type);
	}
}
