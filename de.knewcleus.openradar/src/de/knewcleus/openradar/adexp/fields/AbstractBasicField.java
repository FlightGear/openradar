package de.knewcleus.openradar.adexp.fields;

import de.knewcleus.openradar.adexp.IBasicField;
import de.knewcleus.openradar.adexp.IBasicFieldDescriptor;

public abstract class AbstractBasicField implements IBasicField {
	protected final IBasicFieldDescriptor descriptor;

	public AbstractBasicField(IBasicFieldDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public String getFieldName() {
		return descriptor.getFieldName();
	}
	
	@Override
	public IBasicFieldDescriptor getDescriptor() {
		return descriptor;
	}
}
