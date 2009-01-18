package de.knewcleus.openradar.adexp.impl;

import de.knewcleus.openradar.adexp.IADEXPMessage;
import de.knewcleus.openradar.adexp.IADEXPMessageDescriptor;

public class ADEXPMessage extends AbstractFieldContainer implements IADEXPMessage {
	protected final IADEXPMessageDescriptor descriptor;

	public ADEXPMessage(IADEXPMessageDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public IADEXPMessageDescriptor getDescriptor() {
		return descriptor;
	}
}
