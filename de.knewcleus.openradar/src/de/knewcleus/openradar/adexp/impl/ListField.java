package de.knewcleus.openradar.adexp.impl;

import static java.util.Collections.unmodifiableList;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.knewcleus.openradar.adexp.IField;
import de.knewcleus.openradar.adexp.IListField;

public class ListField implements IListField {
	protected final String fieldName;
	protected final List<IField> fieldList = new Vector<IField>();

	public ListField(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public void append(IField field) {
		fieldList.add(field);
	}
	
	@Override
	public String getFieldName() {
		return fieldName;
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
		String result="-BEGIN"+getFieldName();
		for (IField field: fieldList) {
			result+=" "+field.toString();
		}
		result+="-END "+getFieldName();
		return result;
	}
}
