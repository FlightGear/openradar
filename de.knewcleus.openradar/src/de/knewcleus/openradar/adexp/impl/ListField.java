package de.knewcleus.openradar.adexp.impl;

import de.knewcleus.openradar.adexp.IField;
import de.knewcleus.openradar.adexp.IListField;
import de.knewcleus.openradar.adexp.IListFieldDescriptor;

public class ListField extends AbstractFieldContainer implements IListField {
	protected final IListFieldDescriptor descriptor;
	
	public ListField(IListFieldDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public IListFieldDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public IField get(int index) throws IndexOutOfBoundsException {
		return fieldList.get(index);
	}
	
	@Override
	public String toString() {
		final String name = getDescriptor().getFieldName();
		return "-BEGIN "+name+" "+super.toString()+" -END "+name;
	}
}
