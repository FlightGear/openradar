package de.knewcleus.openradar.adexp.impl;

import de.knewcleus.openradar.adexp.IStructuredField;
import de.knewcleus.openradar.adexp.IStructuredFieldDescriptor;

public class StructuredField extends AbstractFieldContainer implements IStructuredField {
	protected final IStructuredFieldDescriptor descriptor;

	public StructuredField(IStructuredFieldDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public IStructuredFieldDescriptor getDescriptor() {
		return descriptor;
	}
	
	@Override
	public String toString() {
		return "-"+descriptor.getFieldName()+" "+super.toString();
	}
}
