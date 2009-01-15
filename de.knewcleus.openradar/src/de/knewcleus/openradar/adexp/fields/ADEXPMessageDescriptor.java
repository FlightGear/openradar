package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IADEXPMessageDescriptor;

public class ADEXPMessageDescriptor extends AbstractFieldContainerDescriptor
		implements IADEXPMessageDescriptor {
	public ADEXPMessageDescriptor() {
		putField(new BasicFieldDescriptor("TITLE", new TextFieldParser()));
	}
}
