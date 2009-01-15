package de.knewcleus.openradar.adexp.impl;

import de.knewcleus.openradar.adexp.IFieldDescriptor;
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
		String result = "-"+descriptor.getFieldName();
		for (IFieldDescriptor descriptor: getDescriptor()) {
			final String fieldName = descriptor.getFieldName();
			if (!hasField(fieldName)) {
				continue;
			}
			result+=" "+getField(fieldName).toString();
		}
		return result;
	}
}
