package de.knewcleus.openradar.adexp.impl;

import de.knewcleus.openradar.adexp.IADEXPMessage;
import de.knewcleus.openradar.adexp.IADEXPMessageDescriptor;
import de.knewcleus.openradar.adexp.IFieldDescriptor;

public class ADEXPMessage extends AbstractFieldContainer implements IADEXPMessage {
	protected final IADEXPMessageDescriptor descriptor;
	
	public ADEXPMessage(IADEXPMessageDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public IADEXPMessageDescriptor getDescriptor() {
		return descriptor;
	}
	
	@Override
	public String toString() {
		String result = "";
		boolean first = true;
		for (IFieldDescriptor descriptor: getDescriptor()) {
			final String fieldName = descriptor.getFieldName();
			if (!hasField(fieldName)) {
				continue;
			}
			if (!first) {
				result+=" ";
			}
			first=false;
			result+=getField(fieldName).toString();
		}
		return result;
	}
}
