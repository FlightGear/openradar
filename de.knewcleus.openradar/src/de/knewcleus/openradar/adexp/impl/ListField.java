package de.knewcleus.openradar.adexp.impl;

import static java.util.Collections.unmodifiableList;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.knewcleus.openradar.adexp.IField;
import de.knewcleus.openradar.adexp.IListField;
import de.knewcleus.openradar.adexp.IListFieldDescriptor;

public class ListField implements IListField, IFieldRecipient {
	protected final IListFieldDescriptor descriptor;
	protected final List<IField> fieldList = new Vector<IField>();
	
	public ListField(IListFieldDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	@Override
	public void addField(IField field) {
		fieldList.add(field);
	}
	
	@Override
	public IListFieldDescriptor getDescriptor() {
		return descriptor;
	}

	@Override
	public IField get(int index) {
		return fieldList.get(index);
	}

	@Override
	public int size() {
		return fieldList.size();
	}

	@Override
	public Iterator<IField> iterator() {
		return unmodifiableList(fieldList).iterator();
	}

	@Override
	public String toString() {
		String result="-BEGIN"+descriptor.getFieldName();
		for (IField field: fieldList) {
			result+=" "+field.toString();
		}
		result+="-END "+descriptor.getFieldName();
		return result;
	}
}
